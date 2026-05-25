package com.huseyin.personalfinanceapi.category.controller;

import com.huseyin.personalfinanceapi.category.controller.dto.CategoryResponse;
import com.huseyin.personalfinanceapi.category.controller.dto.CreateCategoryRequest;
import com.huseyin.personalfinanceapi.category.controller.dto.UpdateCategoryRequest;
import com.huseyin.personalfinanceapi.category.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> list(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(
                categoryService.listVisibleForUser(userId).stream()
                        .map(CategoryResponse::from)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> get(@PathVariable Long id,
                                                @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(CategoryResponse.from(categoryService.getForUser(id, userId)));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request,
                                                   @AuthenticationPrincipal Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.from(categoryService.create(userId, request)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CategoryResponse> rename(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateCategoryRequest request,
                                                   @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(CategoryResponse.from(categoryService.rename(userId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal Long userId) {
        categoryService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
