package com.huseyin.personalfinanceapi.asset.controller.dto;


import com.huseyin.personalfinanceapi.common.validation.annotation.ValidAssetType;
import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssetRequest(
        @NotBlank String name,
        @NotNull @ValidCurrency String currency,
        @NotNull @ValidAssetType String assetType
) {
}
