package com.example.my.services;

import com.example.my.DTOs.CategoryDTO;

import java.util.List;

public interface CategoryService {
    boolean isExists(Long categoryId);
    List<CategoryDTO> getAllCategories();
    CategoryDTO getById(Long id);
    void add(CategoryDTO categoryDTO);
    void update(CategoryDTO categoryDTO);
}
