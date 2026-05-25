package com.huseyin.personalfinanceapi.category.exception;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("Kategori bulunamadı: id=" + id);
    }
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
