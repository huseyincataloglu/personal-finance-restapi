package com.huseyin.personalfinanceapi.category.controller.dto;

import com.huseyin.personalfinanceapi.category.entity.Category;

public record CategoryResponse(
        Long id,
        String name,
        Category.CategoryType type,
        boolean builtIn,
        boolean active
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getType(), c.isBuiltIn(), c.isActive());
    }
}
