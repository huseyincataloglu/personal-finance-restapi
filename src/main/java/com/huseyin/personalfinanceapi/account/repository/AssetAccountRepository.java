package com.huseyin.personalfinanceapi.account.repository;

import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssetAccountRepository extends JpaRepository<AssetAccount, Long> {

    Optional<AssetAccount> findByIdAndUserId(Long id, Long userId);
}
