package com.library.repository;

import com.library.database.DatabaseConnection;
import com.library.model.Author;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthorRepository implements Repository<Author, Long> {

    private Connection getConnection() {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public Author save(Author author) {
        String sql = """
            INSERT INTO authors (name, nationality, email, biography, created_at, updated_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getNationality());
            ps.setString(3, author.getEmail());
            ps.setString(4, author.getBiography());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                author.setId(keys.getLong(1));
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar autor: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Author> findById(Long id) {
        String sql = "SELECT * FROM authors WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar autor por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Author> findAll() {
        String sql = "SELECT * FROM authors ORDER BY name";
        List<Author> authors = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                authors.add(mapRow(rs));
            }
            return authors;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar autores: " + e.getMessage(), e);
        }
    }

    @Override
    public Author update(Author author) {
        String sql = """
            UPDATE authors SET name = ?, nationality = ?, email = ?, biography = ?,
            updated_at = CURRENT_TIMESTAMP WHERE id = ?
        """;
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, author.getName());
            ps.setString(2, author.getNationality());
            ps.setString(3, author.getEmail());
            ps.setString(4, author.getBiography());
            ps.setLong(5, author.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Autor com ID " + author.getId() + " não encontrado.");
            }
            return author;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar autor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar autor: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM authors WHERE id = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar existência de autor: " + e.getMessage(), e);
        }
    }

    public Optional<Author> findByEmail(String email) {
        String sql = "SELECT * FROM authors WHERE email = ?";
        try (PreparedStatement ps = getConnection().prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar autor por email: " + e.getMessage(), e);
        }
    }

    private Author mapRow(ResultSet rs) throws SQLException {
        Author author = new Author();
        author.setId(rs.getLong("id"));
        author.setName(rs.getString("name"));
        author.setNationality(rs.getString("nationality"));
        author.setEmail(rs.getString("email"));
        author.setBiography(rs.getString("biography"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) author.setCreatedAt(createdAt.toLocalDateTime());
        if (updatedAt != null) author.setUpdatedAt(updatedAt.toLocalDateTime());
        return author;
    }
}
