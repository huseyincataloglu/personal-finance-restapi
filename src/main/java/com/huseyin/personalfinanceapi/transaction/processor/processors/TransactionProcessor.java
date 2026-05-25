package com.huseyin.personalfinanceapi.transaction.processor.processors;


import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.processor.command.TransactionCommand;


public interface TransactionProcessor<T extends TransactionCommand> {

    TransactionType getSupportedType();

    Transaction process(T command);
}
