package com.huseyin.personalfinanceapi.category.service;

import com.huseyin.personalfinanceapi.category.controller.dto.CreateCategoryRequest;
import com.huseyin.personalfinanceapi.category.controller.dto.UpdateCategoryRequest;
import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.category.exception.BuiltInCategoryModificationException;
import com.huseyin.personalfinanceapi.category.exception.CategoryAccessDeniedException;
import com.huseyin.personalfinanceapi.category.exception.CategoryInUseException;
import com.huseyin.personalfinanceapi.category.exception.CategoryNotFoundException;
import com.huseyin.personalfinanceapi.category.repository.CategoryRepository;
import com.huseyin.personalfinanceapi.transaction.repository.UniDirectionalTransactionRepository;
import com.huseyin.personalfinanceapi.user.entity.User;
import com.huseyin.personalfinanceapi.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Kategori yönetimi. Aşağıdaki katı kuralları uygular:
 * <ul>
 *   <li>Built-in (sistem) kategoriler asla değiştirilemez veya silinemez.</li>
 *   <li>Bir kullanıcı kategorisi en az bir Transaction'a atanmışsa silinemez.</li>
 *   <li>Atanmış / atanmamış fark etmeksizin {@code type} (EXPENSE/INCOME) güncellenemez —
 *       yalnızca {@code name} değiştirilebilir.</li>
 *   <li>Built-in kategoriler {@code user_id} null ile, kullanıcı kategorileri
 *       {@code user_id} dolu olarak tutulur.</li>
 * </ul>
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UniDirectionalTransactionRepository uniRepo;
    private final UserRepository userRepository;

    public CategoryService(CategoryRepository categoryRepository,
                           UniDirectionalTransactionRepository uniRepo,
                           UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.uniRepo = uniRepo;
        this.userRepository = userRepository;
    }

    public List<Category> listVisibleForUser(Long userId) {
        return categoryRepository.findVisibleForUser(userId);
    }

    public Category getForUser(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        if (!category.isBuiltIn()) {
            if (!category.getUser().getId().equals(userId)) {
                throw new CategoryAccessDeniedException(
                        "Bu kategoriye erişim yetkiniz yok: id=" + categoryId);
            }
        }
        return category;
    }

    @Transactional
    public Category create(Long userId, CreateCategoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CategoryAccessDeniedException("Kullanıcı bulunamadı"));
        Category c = new Category();
        c.setName(request.name().trim());
        c.setType(Category.CategoryType.valueOf(request.type().toUpperCase()));
        c.setBuiltIn(false);
        c.setUser(user);
        c.setActive(true);
        return categoryRepository.save(c);
    }

    /**
     * Yalnızca {@code name} güncellenir; tip değiştirilemez. Built-in kategoriler
     * üzerinde update yasaktır.
     */
    @Transactional
    public Category rename(Long userId, Long categoryId, UpdateCategoryRequest request) {
        Category category = getForUser(categoryId, userId);
        if (category.isBuiltIn()) {
            throw new BuiltInCategoryModificationException();
        }
        category.setName(request.name().trim());
        return categoryRepository.save(category);
    }

    /**
     * Bir kategori en az bir Transaction'a atanmışsa silinemez. Built-in
     * kategoriler de silinemez. Sadece atanmamış kullanıcı kategorileri silinebilir.
     */
    @Transactional
    public void delete(Long userId, Long categoryId) {
        Category category = getForUser(categoryId, userId);
        if (category.isBuiltIn()) {
            throw new BuiltInCategoryModificationException();
        }
        if (uniRepo.existsByCategoryId(categoryId)) {
            throw new CategoryInUseException(categoryId);
        }
        categoryRepository.delete(category);
    }


}
