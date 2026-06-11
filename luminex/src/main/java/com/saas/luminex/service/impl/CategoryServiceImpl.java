package com.saas.luminex.service.impl;

import com.saas.luminex.entity.Category;
import com.saas.luminex.exception.BadRequestException;
import com.saas.luminex.exception.ResourceNotFoundException;
import com.saas.luminex.repository.CategoryRepository;
import com.saas.luminex.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActive() {
        return categoryRepository.findByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
    }

    @Override
    @Transactional
    public Category create(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new BadRequestException("Category already exists: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category update(Long id, Category incoming) {
        Category existing = getById(id);
        if (!existing.getName().equals(incoming.getName())
                && categoryRepository.existsByName(incoming.getName())) {
            throw new BadRequestException("Category name already taken: " + incoming.getName());
        }
        existing.setName(incoming.getName());
        existing.setIcon(incoming.getIcon());
        existing.setColor(incoming.getColor());
        return categoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category", id);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Category category = getById(id);
        category.setActive(!category.isActive());
        categoryRepository.save(category);
    }
}
