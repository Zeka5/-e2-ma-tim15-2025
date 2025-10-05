package com.ma.ma_backend.service.impl;

import com.ma.ma_backend.domain.Category;
import com.ma.ma_backend.domain.TaskStatus;
import com.ma.ma_backend.domain.User;
import com.ma.ma_backend.dto.CategoryDto;
import com.ma.ma_backend.dto.CreateCategoryRequest;
import com.ma.ma_backend.dto.UpdateCategoryRequest;
import com.ma.ma_backend.exception.BadRequestException;
import com.ma.ma_backend.exception.NotFoundException;
import com.ma.ma_backend.mapper.EntityMapper;
import com.ma.ma_backend.repository.CategoryRepository;
import com.ma.ma_backend.repository.TaskRepository;
import com.ma.ma_backend.service.intr.CategoryService;
import com.ma.ma_backend.service.intr.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final EntityMapper entityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoriesForUser() {
        User user = userService.getLogedInUser();
        return categoryRepository.findByUserId(user.getId())
                .stream()
                .map(entityMapper::categoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        User user = userService.getLogedInUser();
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
        return entityMapper.categoryToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        User user = userService.getLogedInUser();

        // Provera da li već postoji kategorija sa istom bojom za ovog korisnika
        if (categoryRepository.existsByColorAndUserId(request.getColor(), user.getId())) {
            throw new BadRequestException("Category with color " + request.getColor() + " already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .color(request.getColor())
                .user(user)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return entityMapper.categoryToDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
        User user = userService.getLogedInUser();
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        // Ažuriranje naziva
        if (request.getName() != null && !request.getName().isBlank()) {
            category.setName(request.getName());
        }

        // Ažuriranje boje (provera jedinstvenosti)
        if (request.getColor() != null && !request.getColor().isBlank()) {
            if (!request.getColor().equals(category.getColor())) {
                if (categoryRepository.existsByColorAndUserIdAndIdNot(request.getColor(), user.getId(), id)) {
                    throw new BadRequestException("Category with color " + request.getColor() + " already exists");
                }
                category.setColor(request.getColor());
            }
        }

        Category updatedCategory = categoryRepository.save(category);
        return entityMapper.categoryToDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        User user = userService.getLogedInUser();
        Category category = categoryRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new NotFoundException("Category not found with id: " + id));

        // Check if category has any tasks
        if (taskRepository.existsByCategoryId(id)) {
            throw new BadRequestException("Cannot delete category with existing tasks");
        }

        categoryRepository.delete(category);
    }
}
