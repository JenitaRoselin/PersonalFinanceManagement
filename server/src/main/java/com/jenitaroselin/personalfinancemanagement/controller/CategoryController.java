package com.jenitaroselin.personalfinancemanagement.controller;

import com.jenitaroselin.personalfinancemanagement.dto.request.CategoryRequest;
import com.jenitaroselin.personalfinancemanagement.dto.response.CategoryResponse;
import com.jenitaroselin.personalfinancemanagement.enums.CategoryType;
import com.jenitaroselin.personalfinancemanagement.service.CategoryService;
import com.jenitaroselin.personalfinancemanagement.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Exposes REST endpoints for user categories management and type-based filtering.
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        CategoryResponse response = categoryService.createCategory(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(categoryService.getAllCategories(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(categoryService.getCategoryById(userId, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(categoryService.updateCategory(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        categoryService.deleteCategory(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByType(@PathVariable CategoryType type) {
        Long userId = SecurityUtils.getCurrentUserId();
        return ResponseEntity.ok(categoryService.getCategoriesByType(userId, type));
    }
}
