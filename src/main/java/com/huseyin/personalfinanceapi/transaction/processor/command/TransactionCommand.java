package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.time.Instant;

public sealed interface TransactionCommand
        permits AssetPurchaseCommand,
        AssetSellCommand,
        ExchangeCommand,
        ExpenseCommand,
        IncomeCommand,
        InitialBalanceCommand,
        ReverseCommand,
        TransferCommand {
}


