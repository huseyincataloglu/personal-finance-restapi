package com.huseyin.personalfinanceapi.user.dto;

import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.constraints.NotBlank;

public record ChangePreferencesRequest(

        @ValidCurrency
        String baseCurrency
) {
}
