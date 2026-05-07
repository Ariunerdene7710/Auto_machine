package com.example.back.service;

import com.example.back.entity.Category;
import com.example.back.repository.CategoryRepository;
import com.example.back.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CarRepository carRepository;

    public CategoryService(CategoryRepository categoryRepository, CarRepository carRepository) {
        this.categoryRepository = categoryRepository;
        this.carRepository = carRepository;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    public Category createCategory(Category category) {
        String normalizedName = normalizeName(category.getName());

        categoryRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new RuntimeException("Category with name '" + normalizedName + "' already exists");
                });

        Category newCategory = new Category();
        newCategory.setName(normalizedName);
        newCategory.setDescription(normalizeDescription(category.getDescription()));
        newCategory.setCreatedAt(LocalDateTime.now());
        newCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(newCategory);
    }

    public Category updateCategory(Long id, Category category) {
        Category existingCategory = getCategoryById(id);
        String normalizedName = normalizeName(category.getName());

        categoryRepository.findByNameIgnoreCase(normalizedName)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new RuntimeException("Category with name '" + normalizedName + "' already exists");
                });

        existingCategory.setName(normalizedName);
        existingCategory.setDescription(normalizeDescription(category.getDescription()));
        existingCategory.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(existingCategory);
    }

    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        // Fix: Use categoryId field instead of Category relation
        if (carRepository.existsByCategoryId(id)) {
            throw new RuntimeException("Cannot delete category because it is used by existing machines");
        }

        categoryRepository.delete(category);
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }
        return name.trim();
    }

    private String normalizeDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        return description.trim();
    }
}