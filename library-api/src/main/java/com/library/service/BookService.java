package com.library.service;

import com.library.exception.NotFoundException;
import com.library.exception.ValidationException;
import com.library.model.Book;
import com.library.model.Category;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;

import java.util.List;

public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    public Book create(Book book) {
        validate(book);
        ensureAuthorExists(book.getAuthorId());
        Book saved = bookRepository.save(book);
        if (book.getCategoryIds() != null) {
            for (Long catId : book.getCategoryIds()) {
                if (!categoryRepository.existsById(catId)) {
                    throw new NotFoundException("Categoria", catId);
                }
                bookRepository.addCategory(saved.getId(), catId);
            }
        }
        return findById(saved.getId());
    }

    public Book findById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livro", id));
        enrichBook(book);
        return book;
    }

    public List<Book> findAll() {
        List<Book> books = bookRepository.findAll();
        books.forEach(this::enrichBook);
        return books;
    }

    public Book update(Long id, Book updatedData) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Livro", id));

        validate(updatedData);
        ensureAuthorExists(updatedData.getAuthorId());

        existing.setTitle(updatedData.getTitle());
        existing.setIsbn(updatedData.getIsbn());
        existing.setPublicationYear(updatedData.getPublicationYear());
        existing.setPrice(updatedData.getPrice());
        existing.setPages(updatedData.getPages());
        existing.setLanguage(updatedData.getLanguage());
        existing.setAuthorId(updatedData.getAuthorId());
        existing.touch();

        bookRepository.update(existing);
        return findById(existing.getId());
    }

    public boolean delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new NotFoundException("Livro", id);
        }
        return bookRepository.deleteById(id);
    }


    public Book addCategory(Long bookId, Long categoryId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException("Livro", bookId);
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Categoria", categoryId);
        }
        bookRepository.addCategory(bookId, categoryId);
        return findById(bookId);
    }

    public Book removeCategory(Long bookId, Long categoryId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException("Livro", bookId);
        }
        boolean removed = bookRepository.removeCategory(bookId, categoryId);
        if (!removed) {
            throw new NotFoundException("Associação Livro-Categoria", bookId + "/" + categoryId);
        }
        return findById(bookId);
    }

    public List<Category> findCategoriesByBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new NotFoundException("Livro", bookId);
        }
        return categoryRepository.findByBookId(bookId);
    }

    private void enrichBook(Book book) {
        authorRepository.findById(book.getAuthorId())
                .ifPresent(book::setAuthor);
        List<Category> categories = categoryRepository.findByBookId(book.getId());
        book.setCategories(categories);
    }

    private void ensureAuthorExists(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new NotFoundException("Autor", authorId);
        }
    }

    private void validate(Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new ValidationException("O título do livro é obrigatório.");
        }
        if (book.getAuthorId() == null) {
            throw new ValidationException("O ID do autor é obrigatório.");
        }
        if (book.getPublicationYear() != null && (book.getPublicationYear() < 1000 || book.getPublicationYear() > 2100)) {
            throw new ValidationException("Ano de publicação inválido.");
        }
    }
}
