package com.huseyin.personalfinanceapi.category.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateCategoryRequest(
        @NotBlank String name,
        @NotBlank @Pattern(regexp = "EXPENSE|INCOME") String type
) {
}
