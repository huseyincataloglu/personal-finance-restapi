package com.huseyin.personalfinanceapi.transaction.entry;

import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.common.Money;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Bir AssetAccount üzerinde yapılan varlık hareketi (Alım veya Satım).
 * Direction:
 * <ul>
 *   <li>{@code INWARD} — varlığa giriş (alım)</li>
 *   <li>{@code OUTWARD} — varlıktan çıkış (satım)</li>
 * </ul>
 */
@Entity
@Table(name = "asset_entries")
@DiscriminatorValue("ASSET")
@Getter
@Setter
@NoArgsConstructor
public class AssetEntry extends Entry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal quantity;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount",
                    column = @Column(name = "unit_price_amount", precision = 19, scale = 4, nullable = false)),
            @AttributeOverride(name = "currencyCode",
                    column = @Column(name = "unit_price_currency", length = 3, nullable = false))
    })
    private Money unitPrice;

    public AssetEntry(Asset asset, BigDecimal qty, Money unitPrice, Direction direction) {
        this.asset = asset;
        this.quantity = qty;
        this.unitPrice = unitPrice;
        setDirection(direction);
        setEntryType(EntryType.ASSET);
    }

    @Override
    public Entry reverse() {
        AssetEntry reversedAsset = new AssetEntry();
        reversedAsset.setEntryType(EntryType.ASSET);
        reversedAsset.setAccount(this.getAccount());

        reversedAsset.setDirection(this.getDirection().reverse()); // Direction is reversed

        reversedAsset.setAsset(this.asset);
        reversedAsset.setQuantity(this.quantity);
        reversedAsset.setUnitPrice(this.unitPrice);

        return reversedAsset;
    }

}
