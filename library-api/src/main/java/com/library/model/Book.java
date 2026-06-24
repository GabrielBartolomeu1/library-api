package com.library.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Book extends BaseEntity {

    private String title;
    private String isbn;
    private Integer publicationYear;
    private BigDecimal price;
    private Integer pages;
    private String language;

    private Long authorId;
    private transient Author author;

    private transient List<Category> categories = new ArrayList<>();
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Long> categoryIds = new ArrayList<>();

    public Book(Long id, String title, String isbn, Integer publicationYear,
                BigDecimal price, Integer pages, String language, Long authorId) {
        super(id);
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.price = price;
        this.pages = pages;
        this.language = language;
        this.authorId = authorId;
    }

    public Book(String title, String isbn, Integer publicationYear,
                BigDecimal price, Integer pages, String language, Long authorId) {
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.price = price;
        this.pages = pages;
        this.language = language;
        this.authorId = authorId;
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<Category> categories) {
        this.categories = new ArrayList<>(categories);
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(Category category) {
        categories.remove(category);
    }

    @Override
    public String describe() {
        String authorName = author != null ? author.getName() : "Autor ID " + authorId;
        return String.format("Livro: '%s' por %s (%d) — %d página(s), %d categoria(s)",
                title, authorName, publicationYear, pages, categories.size());
    }
}
