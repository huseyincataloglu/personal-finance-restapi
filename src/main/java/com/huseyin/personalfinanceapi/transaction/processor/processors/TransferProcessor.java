package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.TransferCommand;
import com.huseyin.personalfinanceapi.transaction.processor.policy.TransferAccountPolicy;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TransferProcessor implements TransactionProcessor<TransferCommand> {

    private final TransactionEngine engine;
    private final TransactionRepository repository;

    @Override
    public TransactionType getSupportedType() {
        return TransactionType.TRANSFER;
    }

    @Override
    public Transaction process(TransferCommand command) {
        if(command.amount().compareTo(BigDecimal.ZERO) < 0){
            throw new BusinessRuleViolationException("Transfer amount must be positive");
        }
        TransferAccountPolicy.validate(command.source(),command.target());

        BalanceAccount source  =  (BalanceAccount) command.source();
        BalanceAccount destination = (BalanceAccount) command.target();

        Money amount = Money.of(command.amount(),source.getBalance().currencyCode());

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setDescription(command.description() != null? command.description(): "Transfer addition");
        transaction.setTime(command.dateTime());

        CashEntry outEntry = new CashEntry(amount, Entry.Direction.OUTWARD);
        outEntry.setAccount(source);

        CashEntry inEntry = new CashEntry(amount, Entry.Direction.INWARD);
        inEntry.setAccount(destination);

        transaction.addEntry(outEntry);
        transaction.addEntry(inEntry);

        engine.applyCashDelta(source,outEntry);
        engine.applyCashDelta(destination, inEntry);
        return repository.save(transaction);
    }


}
