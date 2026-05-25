package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.IncomeCommand;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import com.huseyin.personalfinanceapi.transaction.processor.policy.IncomeAccountPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class IncomeProcessor implements TransactionProcessor<IncomeCommand>{

    private final TransactionEngine engine;
    private final TransactionRepository repository;


    @Override
    public TransactionType getSupportedType() {
        return TransactionType.INCOME;
    }

    @Override
    public Transaction process(IncomeCommand command) {
        if(command.amount().compareTo(BigDecimal.ZERO) <= 0){
            throw new BusinessRuleViolationException("INCOME amount only must be positive");
        }

        IncomeAccountPolicy.validate(command.account());

        BalanceAccount account= (BalanceAccount) command.account();
        Money amount =  Money.of(command.amount(), account.getBalance().currencyCode());

        if(account.getType() == AccountType.SAVINGS){
            if(!command.category().getName().equalsIgnoreCase("Interest")){
                throw new BusinessRuleViolationException("Income transactions made on savings account can only have Interest category.");
            }
        }

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.INCOME);
        transaction.setDescription(command.description() != null? command.description(): "Income addition");
        transaction.setTime(command.dateTime());
        transaction.setCategory(command.category());

        CashEntry entry = new CashEntry(amount, Entry.Direction.INWARD);
        entry.setAccount(account);
        transaction.addEntry(entry);

        engine.applyCashDelta(account, entry);
        return repository.save(transaction);
    }
}
