package com.huseyin.personalfinanceapi.account.controller.dto;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * Hesap oluşturma isteği. {@code initialBalanceAmount} sağlanırsa, hesap
 * açılışında otomatik olarak INITIAL_BALANCE işlemi de oluşturulur.
 *
 * @param name             Hesabın gösterim adı
 * @param type             AccountType.from() ile parse edilir (WALLET, BANK, ...)
 * @param currencyCode     ISO 4217 (TRY, USD, EUR, ...). Asset hesaplar için null olmalı Çünkü asset varlıklarının
 *   kendi para birimi vardır asset hesabına ait bir para birimi yoktur
 */
public record CreateAccountRequest(
        @NotBlank String name,
        @NotNull AccountType type,
        @ValidCurrency
        String currencyCode
) {
}
