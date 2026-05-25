package com.huseyin.personalfinanceapi.transaction.processor.policy;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;

import java.util.*;

public class TransferAccountPolicy {

    private static final Set<AccountType> INVALID_ACCOUNTS_FOR_TRANSFER =
            EnumSet.of(AccountType.INVESTMENT,AccountType.PRECIOUS_METAL);

    private static final Map<AccountType, List<AccountType>> TRANSFER_ALLOWED_DESTINATIONS =
            Map.of(
                    AccountType.BANK,
                    List.of(AccountType.WALLET,AccountType.BANK,AccountType.SAVINGS,AccountType.RECEIVABLE,AccountType.LOAN,AccountType.CREDIT_CARD,AccountType.DEBT),
                    AccountType.WALLET,
                    List.of(AccountType.WALLET,AccountType.BANK,AccountType.SAVINGS,AccountType.RECEIVABLE,AccountType.LOAN,AccountType.CREDIT_CARD,AccountType.DEBT),
                    AccountType.SAVINGS,
                    List.of(AccountType.WALLET,AccountType.BANK,AccountType.SAVINGS,AccountType.DEBT,AccountType.CREDIT_CARD,AccountType.LOAN),
                    AccountType.RECEIVABLE,
                    List.of(AccountType.WALLET,AccountType.BANK),
                    AccountType.DEBT,
                    List.of(AccountType.WALLET,AccountType.BANK),
                    AccountType.CREDIT_CARD,
                    List.of(AccountType.WALLET,AccountType.BANK),
                    AccountType.LOAN,
                    List.of(AccountType.WALLET,AccountType.BANK)

            );


    private static boolean isAccountAllowed(AccountType type) {
        return !INVALID_ACCOUNTS_FOR_TRANSFER.contains(type);
    }

    public static void  validate(Account source, Account destination) {
        if (!(source instanceof BalanceAccount src)
                || !(destination instanceof BalanceAccount dst)) {
            throw new BusinessRuleViolationException("Transfer transaction can only applicable to accounts having balances");
        }
        if (src.getId().equals(dst.getId())) {
            throw new BusinessRuleViolationException("Source and destination cannot be the same accounts");
        }

        if(!isAccountAllowed(src.getType()) || !isAccountAllowed(dst.getType())){
            List<AccountType> validAccounts = Arrays.stream(AccountType.values()).filter(it -> !INVALID_ACCOUNTS_FOR_TRANSFER.contains(it)).toList();
            throw new BusinessRuleViolationException(
                    "Valid account types for transfer:" + validAccounts.toString()

            );
        }
        if (!src.getBalance().hasSameCurrencyAs(dst.getBalance())){
            throw new BusinessRuleViolationException("Transfer must be between the same currencies");
        }

        if(!TRANSFER_ALLOWED_DESTINATIONS.get(source.getType()).contains(destination.getType())){
            throw new BusinessRuleViolationException(
                    source.getType().name() +" can only transfer to" + TRANSFER_ALLOWED_DESTINATIONS.get(source.getType())
            );
        }


    }
}
