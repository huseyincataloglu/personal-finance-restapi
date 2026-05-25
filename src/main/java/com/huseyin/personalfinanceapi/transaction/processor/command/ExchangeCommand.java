package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;

public record ExchangeCommand(
        User user,
        Account sourceAccount,
        Account destinationAccount,
        BigDecimal sourceAmount,
        BigDecimal destinationAmount,
        BigDecimal appliedRate,
        String description,
        Instant dateTime
)
implements TransactionCommand{
}
