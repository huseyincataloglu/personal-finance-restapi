package com.huseyin.personalfinanceapi.transaction.controller.dto;


import jakarta.validation.constraints.NotNull;

import jakarta.validation.constraints.Positive;


import java.math.BigDecimal;
import java.time.Instant;

public record CreateExpenseRequest(
        @NotNull @Positive Long accountId,
        @NotNull @Positive(message = "Transaction amount must be positive") BigDecimal amount,
        @NotNull @Positive Long categoryId,
        String description,
        @NotNull Instant dateAndTime
) {
}
