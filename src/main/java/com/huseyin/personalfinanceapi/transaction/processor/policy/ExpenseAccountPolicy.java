package com.huseyin.personalfinanceapi.transaction.processor.policy;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import org.springframework.stereotype.Component;

import java.util.EnumSet;


public class ExpenseAccountPolicy {

    private static final EnumSet<AccountType> EXPENSE_NOT_ALLOWED_ACCOUNTS =
            EnumSet.of(
                    AccountType.SAVINGS,AccountType.RECEIVABLE,AccountType.LOAN,AccountType.INVESTMENT,AccountType.PRECIOUS_METAL
            );


    private static boolean isAccountAllowed(AccountType type) {
        return !EXPENSE_NOT_ALLOWED_ACCOUNTS.contains(type);
    }

    public static void validate(Account account) {
        if(!(account instanceof BalanceAccount)){
            throw new BusinessRuleViolationException("Expense transaction needs balance to perform.");
        }

        if(!isAccountAllowed(account.getType())){
            throw new BusinessRuleViolationException(
                    account.getType().name()+" is not suitable for Expense transaction"
            );
        }

    }
}
