package com.ma.ma_backend.service.intr;

import com.ma.ma_backend.dto.CategoryDto;
import com.ma.ma_backend.dto.CreateCategoryRequest;
import com.ma.ma_backend.dto.UpdateCategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategoriesForUser();
    CategoryDto getCategoryById(Long id);
    CategoryDto createCategory(CreateCategoryRequest request);
    CategoryDto updateCategory(Long id, UpdateCategoryRequest request);
    void deleteCategory(Long id);
}
