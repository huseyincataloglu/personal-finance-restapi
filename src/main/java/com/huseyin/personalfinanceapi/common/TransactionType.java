package com.huseyin.personalfinanceapi.common;


import com.huseyin.personalfinanceapi.common.exception.InvalidTransactionTypeException;

import java.util.Arrays;

public enum TransactionType {
    EXPENSE,
    INCOME,
    INITIAL_BALANCE,
    OPENING_ASSET,
    TRANSFER,
    EXCHANGE,
    ASSET_PURCHASE,
    ASSET_SELL,
    REVERSAL,
    REFUND;


    public static TransactionType from(String type){
        return Arrays.stream(TransactionType.values()).filter(it -> it.name().equalsIgnoreCase(type))
                .findFirst().orElseThrow(
                        () -> new InvalidTransactionTypeException("Valid transaction types are:" +
                                "/n" + Arrays.toString(TransactionType.values())
                        )
                );

    }


}
