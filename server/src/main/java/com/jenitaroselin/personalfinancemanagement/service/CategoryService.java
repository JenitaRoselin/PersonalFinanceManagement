package com.jenitaroselin.personalfinancemanagement.service;

import com.jenitaroselin.personalfinancemanagement.dto.request.CategoryRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.CategoryResponse;
import com.jenitaroselin.personalfinancemanagement.entity.Category;
import com.jenitaroselin.personalfinancemanagement.entity.User;
import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import com.jenitaroselin.personalfinancemanagement.exception.BadRequestException;
import com.jenitaroselin.personalfinancemanagement.exception.DuplicateResourceException;
import com.jenitaroselin.personalfinancemanagement.exception.ResourceNotFoundException;
import com.jenitaroselin.personalfinancemanagement.repository.CategoryRepository;
import com.jenitaroselin.personalfinancemanagement.repository.TransactionRepository;
import com.jenitaroselin.personalfinancemanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Manages user category creation, retrieval, updates, and deletion with integrity constraints.
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        if (categoryRepository.existsByUser_UserIdAndNameIgnoreCaseAndType(userId, request.getName().trim(), request.getType())) {
            throw new DuplicateResourceException("Category with this name and type already exists");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = Category.builder()
                .user(user)
                .name(request.getName().trim())
                .type(request.getType())
                .build();

        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories(Long userId) {
        return categoryRepository.findByUser_UserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoriesByType(Long userId, CategoryType type) {
        return categoryRepository.findByUser_UserIdAndType(userId, type).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long userId, Long categoryId) {
        Category category = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return mapToResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!category.getName().equalsIgnoreCase(request.getName().trim()) || category.getType() != request.getType()) {
            if (categoryRepository.existsByUser_UserIdAndNameIgnoreCaseAndType(userId, request.getName().trim(), request.getType())) {
                throw new DuplicateResourceException("Another category with this name and type already exists");
            }
        }

        category.setName(request.getName().trim());
        category.setType(request.getType());
        category = categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findByCategoryIdAndUser_UserId(categoryId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        // Safeguard against deleting categories tied to existing financial records
        if (transactionRepository.existsByCategory_CategoryId(categoryId)) {
            throw new BadRequestException("Cannot delete category '" + category.getName() + "' because it is associated with existing transactions.");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .type(category.getType())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
