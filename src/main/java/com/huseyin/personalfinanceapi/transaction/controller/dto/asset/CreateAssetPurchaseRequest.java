package com.huseyin.personalfinanceapi.transaction.controller.dto.asset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateAssetPurchaseRequest(
        @NotNull @Positive Long cashAccountId,
        @NotNull @Positive Long assetAccountId,
        @NotNull @Positive Long assetId,
        @NotNull @Positive BigDecimal quantity,
        @NotNull @Positive BigDecimal unitPrice,
        @NotBlank String description,
        @NotNull Instant dateAndTime
        ) {
}
