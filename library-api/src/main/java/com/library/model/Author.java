package com.library.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Author extends BaseEntity {

    private String name;
    private String nationality;
    private String email;
    private String biography;

    private transient List<Book> books = new ArrayList<>();

    public Author(Long id, String name, String nationality, String email, String biography) {
        super(id);
        this.name = name;
        this.nationality = nationality;
        this.email = email;
        this.biography = biography;
    }

    public Author(String name, String nationality, String email, String biography) {
        this.name = name;
        this.nationality = nationality;
        this.email = email;
        this.biography = biography;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public void addBook(Book book) {
        if (!books.contains(book)) {
            books.add(book);
        }
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    @Override
    public String describe() {
        return String.format("Autor: %s (%s) — %d livro(s) cadastrado(s)",
                name, nationality, books.size());
    }
}
