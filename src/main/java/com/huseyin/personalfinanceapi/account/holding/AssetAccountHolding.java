package com.huseyin.personalfinanceapi.account.holding;


import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Bir AssetAccount'ın belirli bir varlıktaki ({@code assetSymbol} + {@code assetUnit})
 * birikmiş miktarı ve ağırlıklı ortalama maliyeti. Talimatta belirtilen
 * "AssetHolding" tablosu — geriye dönük uyumluluk için sınıf adı korunmuştur.
 *
 * <p>Bir varlık alımı/satımında miktar ve ortalama maliyet bu kayıt üzerinde
 * atomik şekilde güncellenir; satışta tüm pozisyon kapanırsa kayıt silinir.
 */
@Entity
@Table(name = "asset_holdings",
        indexes = @Index(name = "idx_holding_account", columnList = "account_id"),
        uniqueConstraints = @UniqueConstraint(
                name = "uk_holding_account_asset",
                columnNames = {"account_id", "asset_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class AssetAccountHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AssetAccount account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id",nullable = false)
    private Asset asset;

    @Column(nullable = false,precision = 18,scale = 4)
    private BigDecimal quantity;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal averageUnitPrice;

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal totalCost;

}
