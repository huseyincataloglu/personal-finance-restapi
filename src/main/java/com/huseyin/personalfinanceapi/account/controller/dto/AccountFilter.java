package com.huseyin.personalfinanceapi.account.controller.dto;

import com.huseyin.personalfinanceapi.account.AccountType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Account listeleme/arama için filtre. Bakiye filtresi yalnızca
 * BalanceAccount'lar üzerinde anlamlı; AssetAccount'lar otomatik kapsam dışı
 * kalır (balance kolonu yoktur).
 */
public record AccountFilter(
        List<AccountType> types,
        String currencyCode,
        Boolean active,
        String nameContains,
        BigDecimal balanceMin,
        BigDecimal balanceMax
) {
}
