package com.huseyin.personalfinanceapi.transaction.controller.dto.asset;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;


public record OpeningAssetRequest(
        @NotNull Long assetAccountId,
        @NotEmpty List<AssetItemLine> assetItemLineList
) {


    public record AssetItemLine(
            @NotNull @Positive Long assetId,   // Varlığın sistemdeki ID'si (Örn: THYAO, Altın)
            @NotNull @Positive BigDecimal quantity,// Miktar (Örn: 10 adet veya 5.5 gram)
            @NotNull @Positive BigDecimal unitPrice// Alış birim fiyatı (Maliyet hesabı için)
    ) {}

}
