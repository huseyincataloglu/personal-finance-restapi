package com.huseyin.personalfinanceapi.transaction.processor.policy;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class AssetSellAccountPolicy {

    private static final Set<AccountType> VALID_ACCOUNTS_FOR_ASSET_TRANSACTIONS =
            EnumSet.of(AccountType.WALLET, AccountType.BANK, AccountType.SAVINGS, AccountType.INVESTMENT, AccountType.PRECIOUS_METAL);

    private static final Set<AccountType> ASSET_SELL_ALLOWED_SOURCES =
            EnumSet.of(AccountType.INVESTMENT, AccountType.PRECIOUS_METAL);

    private static final Set<AccountType> ASSET_SELL_ALLOWED_DESTINATIONS =
            EnumSet.of(AccountType.WALLET, AccountType.BANK,
                    AccountType.SAVINGS);



    public static boolean isAccountAllowed(AccountType type) {
        return VALID_ACCOUNTS_FOR_ASSET_TRANSACTIONS.contains(type);
    }

    public static void validate(Account source, Account destination) {
        if (source.getId().equals(destination.getId())) {
            throw new BusinessRuleViolationException("Source and destination cannot be the same");
        }
        if(!(source instanceof AssetAccount) || !(destination instanceof BalanceAccount)){
            throw new BusinessRuleViolationException("Source and destination cannot be the same");
        }

        if (!isAccountAllowed(source.getType()) || !isAccountAllowed(destination.getType())) {
            throw new BusinessRuleViolationException(
                    "Valıd accounts for asset sell transaction:" + VALID_ACCOUNTS_FOR_ASSET_TRANSACTIONS.stream().toList()
            );
        }
        if (!ASSET_SELL_ALLOWED_SOURCES.contains(source.getType())) {
            throw new BusinessRuleViolationException(
                    source.getType().name() + "cannot be source account in asset-sell transaction"
            );
        }
        if (!ASSET_SELL_ALLOWED_DESTINATIONS.contains(destination.getType())) {
            throw new BusinessRuleViolationException(
                    destination.getType().name() + "cannot be destination account in asset-sell transaction"
            );
        }

    }
}
