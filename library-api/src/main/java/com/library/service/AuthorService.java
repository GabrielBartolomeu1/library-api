package com.library.service;

import com.library.exception.NotFoundException;
import com.library.exception.ValidationException;
import com.library.model.Author;
import com.library.model.Book;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;

import java.util.List;

public class AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public Author create(Author author) {
        validate(author);
        authorRepository.findByEmail(author.getEmail()).ifPresent(existing -> {
            throw new ValidationException("Já existe um autor com o email: " + author.getEmail());
        });
        return authorRepository.save(author);
    }

    public Author findById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor", id));
        List<Book> books = bookRepository.findByAuthorId(id);
        books.forEach(author::addBook);
        return author;
    }

    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    public Author update(Long id, Author updatedData) {
        Author existing = authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Autor", id));

        validate(updatedData);

        authorRepository.findByEmail(updatedData.getEmail()).ifPresent(other -> {
            if (!other.getId().equals(id)) {
                throw new ValidationException("Email já está em uso por outro autor.");
            }
        });

        existing.setName(updatedData.getName());
        existing.setNationality(updatedData.getNationality());
        existing.setEmail(updatedData.getEmail());
        existing.setBiography(updatedData.getBiography());
        existing.touch();

        return authorRepository.update(existing);
    }

    public boolean delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new NotFoundException("Autor", id);
        }
        return authorRepository.deleteById(id);
    }

    public List<Book> findBooksByAuthor(Long authorId) {
        if (!authorRepository.existsById(authorId)) {
            throw new NotFoundException("Autor", authorId);
        }
        return bookRepository.findByAuthorId(authorId);
    }

    private void validate(Author author) {
        if (author.getName() == null || author.getName().isBlank()) {
            throw new ValidationException("O nome do autor é obrigatório.");
        }
        if (author.getEmail() == null || author.getEmail().isBlank()) {
            throw new ValidationException("O email do autor é obrigatório.");
        }
        if (!author.getEmail().contains("@")) {
            throw new ValidationException("O email informado é inválido.");
        }
    }
}
