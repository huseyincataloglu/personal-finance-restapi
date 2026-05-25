package com.huseyin.personalfinanceapi.transaction.resolver;

import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.category.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryResolver {

    private final CategoryService service;


    public CategoryResolver(CategoryService service) {
        this.service = service;
    }

    public Category resolveForTransaction(Long categoryId, Long userId) {

        Category category = service.getForUser(categoryId, userId);

        if (!category.isActive()) {
            throw new IllegalStateException("Pasif kategori kullanılamaz: id=" + categoryId);
        }
        return category;
    }

}
