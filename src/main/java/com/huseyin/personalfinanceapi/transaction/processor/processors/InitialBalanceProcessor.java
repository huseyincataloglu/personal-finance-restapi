package com.huseyin.personalfinanceapi.transaction.processor.processors;


import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.InitialBalanceCommand;
import com.huseyin.personalfinanceapi.transaction.processor.policy.InitialBalAccountPolicy;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialBalanceProcessor implements TransactionProcessor<InitialBalanceCommand> {
    private final TransactionEngine engine;
    private final TransactionRepository repository;
    private final InitialBalAccountPolicy policy;
    @Override
    public TransactionType getSupportedType() {
        return TransactionType.INITIAL_BALANCE;
    }

    @Override
    public Transaction process(InitialBalanceCommand command) {
        if(command.amount().compareTo(BigDecimal.ZERO)<= 0){
            throw new BusinessRuleViolationException("Initial balance amount must be positive.");
        }

        policy.validate(command.account());
        BalanceAccount account =(BalanceAccount) command.account();
        Money amount = Money.of(command.amount(), account.getBalance().currencyCode());

        Transaction tx = new Transaction();
        tx.setUser(command.user());
        tx.setType(TransactionType.INITIAL_BALANCE);
        tx.setDescription( "Account Initial Balance");
        tx.setTime(Instant.from(account.getCreatedAt().plusMinutes(1)));
        CashEntry entry;

        entry = resolveEntryDirection(account, amount);// !
        entry.setAccount(account);
        tx.addEntry(entry);


        engine.applyEntries(tx.getEntryList());// single entry

        return repository.save(tx);
    }

    /**
     * Initial balance increases liabilities for a liability account which means entry direction must be outward.
     * However, non-liability account's entry direction will be inward
     * @param account
     * @param amount
     * @return
     */
    private static CashEntry resolveEntryDirection(BalanceAccount account, Money amount) {
        CashEntry entry;
        if(account.getType().isLiability()){
            entry = new CashEntry(amount, Entry.Direction.OUTWARD);
        }
        else {
            entry = new CashEntry(amount, Entry.Direction.INWARD);
        }
        return entry;
    }
}
