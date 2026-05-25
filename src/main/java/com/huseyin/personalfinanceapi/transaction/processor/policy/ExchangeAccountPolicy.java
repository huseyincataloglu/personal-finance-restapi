package com.huseyin.personalfinanceapi.transaction.processor.policy;


import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;

import java.util.EnumSet;

public class ExchangeAccountPolicy {

    private static final EnumSet<AccountType> VALID_ACCOUNTS_FOR_EXCHANGE =
            EnumSet.of(AccountType.BANK,AccountType.WALLET,AccountType.SAVINGS);


    private boolean isAccountAllowed(AccountType type) {
        return VALID_ACCOUNTS_FOR_EXCHANGE.contains(type);
    }


    public void validate(Account source, Account destination) {
        if (!(source instanceof BalanceAccount src)
                || !(destination instanceof BalanceAccount dst)) {
            throw new BusinessRuleViolationException("Exchange transaction can only applicable to accounts having balances");
        }

        if (src.getId().equals(dst.getId())) {
            throw new BusinessRuleViolationException("Source and destination cannot be the same for Exchange");
        }
        if (!isAccountAllowed(source.getType())
                || !isAccountAllowed(destination.getType())) {
            throw new BusinessRuleViolationException(
                    "Exchange is only allowed for accounts of" + VALID_ACCOUNTS_FOR_EXCHANGE.stream().map(Enum::name).toList());
        }

        if (src.getBalance().hasSameCurrencyAs(dst.getBalance())) {
            throw new BusinessRuleViolationException(
                    "EXCHANGE for differenct currencies; perform transfer for the same currencies");
        }
    }
}
