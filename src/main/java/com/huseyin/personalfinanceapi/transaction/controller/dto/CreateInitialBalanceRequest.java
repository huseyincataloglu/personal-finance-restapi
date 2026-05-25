package com.huseyin.personalfinanceapi.transaction.controller.dto;

import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateInitialBalanceRequest(
        @NotNull Long accountId,
        @NotNull @Positive BigDecimal amount,
        String description
) {
}
