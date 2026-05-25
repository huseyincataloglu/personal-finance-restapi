package com.huseyin.personalfinanceapi.category.repository;

import com.huseyin.personalfinanceapi.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /** Built-in (sistem) + bu kullanıcının kendi kategorileri. */
    @Query("""
            SELECT c FROM Category c
             WHERE (c.builtIn = true OR c.user.id = :userId)
               AND c.active = true
            """)
    List<Category> findVisibleForUser(@Param("userId") Long userId);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCaseAndBuiltInTrue(String name);
}
