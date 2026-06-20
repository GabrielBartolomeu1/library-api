package com.library.service;

import com.library.exception.NotFoundException;
import com.library.exception.ValidationException;
import com.library.model.Category;
import com.library.repository.CategoryRepository;

import java.util.List;

public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category create(Category category) {
        validate(category);
        return categoryRepository.save(category);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria", id));
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category update(Long id, Category updatedData) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Categoria", id));

        validate(updatedData);

        existing.setName(updatedData.getName());
        existing.setDescription(updatedData.getDescription());
        existing.touch();

        return categoryRepository.update(existing);
    }

    public boolean delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Categoria", id);
        }
        return categoryRepository.deleteById(id);
    }

    private void validate(Category category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ValidationException("O nome da categoria é obrigatório.");
        }
    }
}
