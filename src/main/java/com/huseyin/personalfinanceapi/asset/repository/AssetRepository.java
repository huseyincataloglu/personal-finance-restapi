package com.huseyin.personalfinanceapi.asset.repository;

import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset,Long> {

    @Query("SELECT a FROM Asset a WHERE a.isBuiltIn = true OR a.user.id = :userId")
    List<Asset> findSystemAndUserAssets(@Param("userId") Long userId);

    @Query("SELECT a FROM Asset a WHERE a.isBuiltIn = false AND a.user.id = :userId")
    List<Asset> findOnlyUserCreatedAssets(@Param("userId") Long userId);

    boolean existsByNameAndCurrencyAndType(String name, String currency, Asset.AssetType type);

    @Query("SELECT COUNT(a) > 0 FROM Asset a WHERE " +
            "a.name = :name AND a.currency = :currency AND a.type = :type " +
            "AND (a.isBuiltIn = true OR a.user.id = :userId)")
    boolean existsForSystemOrUser(
            @Param("name") String name,
            @Param("type") Asset.AssetType type,
            @Param("currency") String currency,
            @Param("userId") Long userId
    );


}
