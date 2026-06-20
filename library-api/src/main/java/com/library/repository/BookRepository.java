package com.library.repository;

import com.library.database.DatabaseConnection;
import com.library.model.Book;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements Repository<Book, Long> {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Book save(Book book) {
        String sql = """
            INSERT INTO books (title, isbn, publication_year, price, pages, language, author_id, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt(3, book.getPublicationYear());
            ps.setBigDecimal(4, book.getPrice());
            ps.setInt(5, book.getPages());
            ps.setString(6, book.getLanguage());
            ps.setLong(7, book.getAuthorId());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                book.setId(keys.getLong(1));
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livro por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar livros: " + e.getMessage(), e);
        }
    }

    @Override
    public Book update(Book book) {
        String sql = """
            UPDATE books SET title = ?, isbn = ?, publication_year = ?, price = ?,
            pages = ?, language = ?, author_id = ?, updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getIsbn());
            ps.setInt(3, book.getPublicationYear());
            ps.setBigDecimal(4, book.getPrice());
            ps.setInt(5, book.getPages());
            ps.setString(6, book.getLanguage());
            ps.setLong(7, book.getAuthorId());
            ps.setLong(8, book.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) throw new RuntimeException("Livro com ID " + book.getId() + " não encontrado.");
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar livro: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM books WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar livro: " + e.getMessage(), e);
        }
    }

    /**
     * Busca todos os livros de um autor específico (relacionamento 1:N).
     */
    public List<Book> findByAuthorId(Long authorId) {
        String sql = "SELECT * FROM books WHERE author_id = ? ORDER BY title";
        List<Book> books = new ArrayList<>();
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, authorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar livros do autor: " + e.getMessage(), e);
        }
    }

    // ─── Gerenciamento N:N: book_categories ───────────────────────────────────

    /**
     * Adiciona uma categoria a um livro (N:N).
     */
    public void addCategory(Long bookId, Long categoryId) {
        String sql = "MERGE INTO book_categories (book_id, category_id) KEY (book_id, category_id) VALUES (?, ?)";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ps.setLong(2, categoryId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar categoria ao livro: " + e.getMessage(), e);
        }
    }

    /**
     * Remove uma categoria de um livro (N:N).
     */
    public boolean removeCategory(Long bookId, Long categoryId) {
        String sql = "DELETE FROM book_categories WHERE book_id = ? AND category_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ps.setLong(2, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover categoria do livro: " + e.getMessage(), e);
        }
    }

    /**
     * Remove todas as categorias de um livro.
     */
    public void clearCategories(Long bookId) {
        String sql = "DELETE FROM book_categories WHERE book_id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, bookId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao limpar categorias do livro: " + e.getMessage(), e);
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublicationYear(rs.getInt("publication_year"));
        BigDecimal price = rs.getBigDecimal("price");
        book.setPrice(price);
        book.setPages(rs.getInt("pages"));
        book.setLanguage(rs.getString("language"));
        book.setAuthorId(rs.getLong("author_id"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) book.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null) book.setUpdatedAt(updatedAt.toLocalDateTime());
        return book;
    }
}
