package com.huseyin.personalfinanceapi.transaction.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * EXCHANGE: yalnızca Cüzdan / Banka / Birikim hesapları arasında, farklı para birimi
 * kombinasyonlarında. {@code appliedRate}, kaynak para birimini hedef para
 * birimine çevirir: {@code destAmount = sourceAmount * appliedRate}.
 */
public record CreateExchangeRequest(
        @NotNull @Positive Long sourceAccountId,
        @NotNull @Positive Long destinationAccountId,
        @NotNull @Positive BigDecimal destinationAmount,
        @NotNull @Positive BigDecimal sourceAmount,
        @Positive BigDecimal appliedRate,
        String description,
        @NotNull Instant dateAndTime
) {
}
