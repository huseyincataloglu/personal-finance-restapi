package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;

public record InitialBalanceCommand(
        User user,
        Account account,
        BigDecimal amount
) implements TransactionCommand
{
}
