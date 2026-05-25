package com.huseyin.personalfinanceapi.category.exception;

/**
 * Sistem (built-in) kategorisi üzerinde değişiklik veya silme yapılmaya
 * çalışıldığında fırlatılır.
 */
public class BuiltInCategoryModificationException extends RuntimeException {
    public BuiltInCategoryModificationException() {
        super("Sistem (built-in) kategorisi değiştirilemez veya silinemez");
    }
}
