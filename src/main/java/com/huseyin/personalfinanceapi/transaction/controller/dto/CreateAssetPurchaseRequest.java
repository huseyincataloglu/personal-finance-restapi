package com.huseyin.personalfinanceapi.transaction.controller.dto;

import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Asset Purchase: kaynak hesap (Cüzdan / Banka / Birikim / Alacak), hedef
 * hesap zorunlu olarak bir AssetAccount (Kıymetli Maden / Yatırım) olmalıdır.
 *
 * <p>Toplam ödenen nakit = quantity * unitPrice. Borç hesaplarıyla varlık
 * alımı yapılamaz.
 */
public record CreateAssetPurchaseRequest(
        @NotNull Long sourceCashAccountId,
        @NotNull Long assetAccountId,
        @NotNull @Positive Long assetId,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal unitPriceAmount,
        String description,
        @NotNull Instant dateAndTime
) {
}
