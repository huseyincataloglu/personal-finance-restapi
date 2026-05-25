package com.huseyin.personalfinanceapi.account.controller.dto;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.holding.AssetAccountHolding;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record AccountResponse(
        Long id,
        String name,
        AccountType type,
        String currencyCode,
        boolean active,
        BigDecimal balance,
        List<HoldingDto> holdings,
        LocalDateTime createdAt
) {
    public record HoldingDto(
            Long id,
            String assetSymbol,
            String assetUnit,
            BigDecimal quantity,
            BigDecimal averageUnitPriceAmount,
            String averageUnitPriceCurrency
    ) {}

    public static AccountResponse from(Account a) {
        BigDecimal balance = null;
        List<HoldingDto> holdings = List.of();

        if (a instanceof BalanceAccount ba && ba.getBalance() != null) {
            balance = ba.getBalance().amount();
        } else if (a instanceof AssetAccount aa) {
            holdings = aa.getHoldings().stream()
                    .map(AccountResponse::toHolding)
                    .toList();
        }
        return new AccountResponse(
                a.getId(), a.getName(), a.getType(), a.getCurrencyCode(),
                a.isActive(), balance, holdings, a.getCreatedAt()
        );
    }

    private static HoldingDto toHolding(AssetAccountHolding h) {
        return new HoldingDto(
                h.getId(),
                h.getAssetSymbol(),
                h.getAssetUnit(),
                h.getQuantity(),
                h.getAverageUnitPrice() != null ? h.getAverageUnitPrice().amount() : null,
                h.getAverageUnitPrice() != null ? h.getAverageUnitPrice().currencyCode() : null
        );
    }
}
