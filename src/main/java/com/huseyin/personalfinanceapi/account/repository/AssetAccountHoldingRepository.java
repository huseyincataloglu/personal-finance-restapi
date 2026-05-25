package com.huseyin.personalfinanceapi.account.repository;

import com.huseyin.personalfinanceapi.account.holding.AssetAccountHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetAccountHoldingRepository extends JpaRepository<AssetAccountHolding, Long> {

    List<AssetAccountHolding> findAllByAccountId(Long accountId);

    Optional<AssetAccountHolding> findByAccountIdAndAssetSymbolAndAssetUnit(
            Long accountId, String assetSymbol, String assetUnit);
}
