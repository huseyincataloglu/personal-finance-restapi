package com.huseyin.personalfinanceapi.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.huseyin.personalfinanceapi.account.exception.InvalidAccountTypeException;
import com.huseyin.personalfinanceapi.common.Money;

import java.util.Arrays;

public enum AccountType {
    WALLET,
    BANK,
    SAVINGS,
    RECEIVABLE,
    CREDIT_CARD,
    LOAN,
    DEBT,
    PRECIOUS_METAL,
    INVESTMENT;

    /** Accounts that track asset holdings rather than a scalar balance. */
    public boolean isAssetBased() {
        return this == PRECIOUS_METAL || this == INVESTMENT;
    }

    /** Liability accounts reduce net worth even when their balance is positive. */
    public boolean isLiability() {
        return this == CREDIT_CARD || this == LOAN || this == DEBT;
    }

    @JsonCreator
    public static AccountType from(String value) {
        for (AccountType accountType : AccountType.values()) {
            if (accountType.name().equalsIgnoreCase(value)) {
                return accountType;
            }
        }
        throw new InvalidAccountTypeException(value + " geçersiz bir hesap türü"
                + "\n"
                + "Geçerli hesap türleri: " + Arrays.toString(AccountType.values()));
    }
}