package com.huseyin.personalfinanceapi.transaction.controller.dto;

import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Asset Sell: kaynak hesap zorunlu olarak bir AssetAccount; hedef hesap
 * Cüzdan / Banka / Birikim olmalıdır (alacak hesabına satıştan elde edilen
 * nakit aktarmak iş kuralları içinde tanımlı değil — varsayılan: Cüzdan/Banka/Birikim).
 */
public record CreateAssetSellRequest(
        @NotNull Long assetAccountId,
        @NotNull Long destinationCashAccountId,
        @NotBlank String assetSymbol,
        @NotBlank String assetUnit,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal unitPriceAmount,
        @NotNull @Positive BigDecimal totalCashAmount,
        @NotBlank @ValidCurrency String unitPriceCurrencyCode,
        String description,
        @NotNull Instant dateAndTime
) {
}
