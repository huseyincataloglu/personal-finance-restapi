package com.huseyin.personalfinanceapi.transaction.controller.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateIncomeRequest(
        @NotNull @Positive Long accountId,
        @NotNull @Positive BigDecimal amount,
        @NotNull @Positive Long categoryId,
        String description,
        @NotNull Instant dateAndTime
) {
}
