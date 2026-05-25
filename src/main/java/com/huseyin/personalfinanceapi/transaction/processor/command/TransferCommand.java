package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;

public record    TransferCommand(
        User user,
        Account source,
        Account target,
        BigDecimal amount,
        String description,
        Instant dateTime
) implements TransactionCommand {
}
