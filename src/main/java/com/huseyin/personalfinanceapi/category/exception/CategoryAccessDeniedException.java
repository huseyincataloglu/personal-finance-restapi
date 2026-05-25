package com.huseyin.personalfinanceapi.category.exception;

public class CategoryAccessDeniedException extends RuntimeException {
    public CategoryAccessDeniedException(String message) {
        super(message);
    }
}
