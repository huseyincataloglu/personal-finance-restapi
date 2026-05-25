package com.huseyin.personalfinanceapi.account.repository;

import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceAccountRepository extends JpaRepository<BalanceAccount, Long> {

    Optional<BalanceAccount> findByIdAndUserId(Long id, Long userId);
}
