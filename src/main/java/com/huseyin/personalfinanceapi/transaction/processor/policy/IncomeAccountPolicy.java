package com.huseyin.personalfinanceapi.transaction.processor.policy;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;

import java.util.EnumSet;

public class IncomeAccountPolicy {

    private final static EnumSet<AccountType> INCOME_ALLOWED_ACCOUNTS =
            EnumSet.of(
                    AccountType.WALLET,AccountType.BANK,AccountType.RECEIVABLE,AccountType.SAVINGS
            );


    public static boolean isAccountAllowed(AccountType type) {
        return INCOME_ALLOWED_ACCOUNTS.contains(type);
    }

    public static void validate(Account account) {
        if(!(account instanceof BalanceAccount)){
            throw new BusinessRuleViolationException("Income transaction needs balance to perform.");
        }

        if(!isAccountAllowed(account.getType())){
            throw new BusinessRuleViolationException(
                    account.getType()+"is not allowed for Income transactions"
            );
        }
    }

}
