package com.huseyin.personalfinanceapi.category.exception;

/**
 * Bir kategori en az bir Transaction ile ilişkilendirildikten sonra silinemez.
 * Bu istisna, böyle bir silme isteğinde fırlatılır.
 */
public class CategoryInUseException extends RuntimeException {
    public CategoryInUseException(Long categoryId) {
        super("Kategori en az bir işleme atanmış olduğu için silinemez: id=" + categoryId);
    }
}
