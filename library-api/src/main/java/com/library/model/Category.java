package com.library.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class Category extends BaseEntity {

    private String name;
    private String description;

    public Category(Long id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String describe() {
        return String.format("Categoria: %s — %s", name, description);
    }
}
