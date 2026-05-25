package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.ExpenseCommand;
import com.huseyin.personalfinanceapi.transaction.processor.policy.ExpenseAccountPolicy;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ExpenseProcessor implements TransactionProcessor<ExpenseCommand>{

    private final TransactionEngine engine;
    private final TransactionRepository repository;

    @Override
    public TransactionType getSupportedType() {
        return TransactionType.EXPENSE;
    }

    @Override
    public Transaction process(ExpenseCommand command) {
        if(command.amount().compareTo(BigDecimal.ZERO)<= 0){
            throw new BusinessRuleViolationException("Expense amount must be positive.");
        }
        ExpenseAccountPolicy.validate(command.account());
        BalanceAccount account = (BalanceAccount) command.account();

        Money amount =  Money.of(command.amount(), account.getBalance().currencyCode());

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.EXPENSE);
        transaction.setDescription(command.description() != null? command.description(): "expense addition");
        transaction.setTime(command.dateTime());
        transaction.setCategory(command.category());

        CashEntry entry = new CashEntry(amount, Entry.Direction.OUTWARD);
        entry.setAccount(account);
        transaction.addEntry(entry);

        engine.applyCashDelta(account, entry);
        return repository.save(transaction);
    }



}
