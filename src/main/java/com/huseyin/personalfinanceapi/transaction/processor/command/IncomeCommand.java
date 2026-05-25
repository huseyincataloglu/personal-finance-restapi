package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.transaction.controller.dto.CreateIncomeRequest;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;

public record IncomeCommand(
        User user,
        Account account,
        BigDecimal amount,
        String description,
        Instant dateTime,
        Category category
)
        implements TransactionCommand {

}
