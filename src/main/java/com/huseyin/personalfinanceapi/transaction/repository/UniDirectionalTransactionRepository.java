package com.huseyin.personalfinanceapi.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniDirectionalTransactionRepository
        extends JpaRepository<UniDirectionalTransaction, Long> {

    boolean existsByCategoryId(Long categoryId);

    List<UniDirectionalTransaction> findByUserIdAndCategoryId(Long userId, Long categoryId);
}
