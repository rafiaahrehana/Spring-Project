package com.saas.luminex.service;

import com.saas.luminex.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllActive();
    List<Category> getAll();
    Category getById(Long id);
    Category create(Category category);
    Category update(Long id, Category category);
    void delete(Long id);
    void toggleActive(Long id);
}
