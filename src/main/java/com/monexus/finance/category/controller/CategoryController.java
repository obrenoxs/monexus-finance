package com.monexus.finance.category.controller;

import com.monexus.finance.category.dto.request.CategoryRequest;
import com.monexus.finance.category.dto.response.CategoryResponse;
import com.monexus.finance.category.service.CategoryService;
import com.monexus.finance.user.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(userDetails.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CategoryResponse> response = categoryService.getCategories(userDetails.getUser());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        CategoryResponse response = categoryService.getCategoryById(userDetails.getUser(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(userDetails.getUser(), id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long id) {
        categoryService.deleteCategory(userDetails.getUser(), id);
        return ResponseEntity.noContent().build();
    }
}
