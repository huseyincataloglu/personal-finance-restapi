package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.transaction.entity.Transaction;

public record ReverseCommand(
        Transaction originalTransaction
) implements TransactionCommand {
}
