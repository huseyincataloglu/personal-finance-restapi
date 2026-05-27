package com.huseyin.personalfinanceapi.account.entity;


import com.huseyin.personalfinanceapi.account.holding.AssetAccountHolding;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Kıymetli Maden veya Yatırım hesabı. Skaler bakiyesi yoktur; bunun yerine
 * {@link AssetAccountHolding} kayıtlarıyla varlık miktarları takip edilir.
 *
 * <p>Yalnızca {@code ASSET_PURCHASE} ve {@code ASSET_SELL} işlem tipleriyle
 * çalışır (TransactionValidator'da zorlanır).
 */
@Entity
@Table(name = "asset_accounts")
@DiscriminatorValue("ASSET")
@Getter
@Setter
@NoArgsConstructor
public class AssetAccount extends Account {

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AssetAccountHolding> holdings = new ArrayList<>();

    public AssetAccountHolding findHolding(Asset asset) {
        Optional<AssetAccountHolding> holding =  holdings.stream()
                .filter(h -> h.getAsset().getId().equals(asset.getId()))
                .findFirst();
        return holding.orElse(null);

    }

    public boolean existsHolding(Asset asset){
        return findHolding(asset) == null;
    }

    public AssetAccountHolding initializeHolding(
            Asset asset
    ){
        AssetAccountHolding holding = new AssetAccountHolding();
        holding.setAsset(asset);
        holding.setAccount(this);
        holding.setQuantity(BigDecimal.ZERO);
        holding.setTotalCost(BigDecimal.ZERO);
        holding.setAverageUnitPrice(BigDecimal.ZERO);
        this.holdings.add(holding);
        return holding;
    }


    public void addHolding(AssetAccountHolding holding) {
        holding.setAccount(this);
        holdings.add(holding);
    }

    public void removeHolding(AssetAccountHolding holding) {
        holdings.remove(holding);
        holding.setAccount(null);
    }



}
