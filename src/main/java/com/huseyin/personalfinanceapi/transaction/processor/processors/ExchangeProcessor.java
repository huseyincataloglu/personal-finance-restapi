package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.ExchangeCommand;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class ExchangeProcessor implements TransactionProcessor<ExchangeCommand> {

    private final TransactionEngine engine;
    private final TransactionRepository repository;

    @Override
    public TransactionType getSupportedType() {
        return TransactionType.EXCHANGE;
    }

    @Override
    public Transaction process(ExchangeCommand command) {
        // 1. Kur bilgisini belirle (Yoksa hesapla, varsa doğrula)
        BigDecimal finalRate;

        if (command.appliedRate() == null) {
            // Kullanıcı kur göndermediyse: Hedef / Kaynak formülü ile kuru sistem hesaplar
            finalRate = command.destinationAmount()
                    .divide(command.sourceAmount(), 6, RoundingMode.HALF_UP);
        } else {
            // Kullanıcı kur gönderdiyse: Tutarlılığı doğrula ve gönderilen kuru kullan
            verifyAmounts(command.sourceAmount(), command.destinationAmount(), command.appliedRate());
            finalRate = command.appliedRate();
        }

        BalanceAccount source  =  (BalanceAccount) command.sourceAccount();
        BalanceAccount destination = (BalanceAccount) command.destinationAccount();

        Money sourceMoney = Money.of(command.sourceAmount(),source.getBalance().currencyCode());
        Money destMoney = Money.of(command.destinationAmount(),destination.getBalance().currencyCode());

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.EXCHANGE);
        transaction.setDescription(command.description() != null? command.description(): "Exchange transaction");
        transaction.setTime(command.dateTime());
        transaction.setAppliedRate(finalRate);

        CashEntry sourceEntry = new CashEntry(sourceMoney, Entry.Direction.OUTWARD);
        sourceEntry.setAccount(source);

        CashEntry destinationEntry = new CashEntry(destMoney, Entry.Direction.INWARD);
        destinationEntry.setAccount(destination);

        transaction.addEntry(sourceEntry);
        transaction.addEntry(destinationEntry);

        engine.applyCashDelta(source,sourceEntry);
        engine.applyCashDelta(destination, destinationEntry);
        return repository.save(transaction);

        
    }

    private void verifyAmounts(BigDecimal sourceAmount,BigDecimal destAmount,BigDecimal appliedRate){
        if (sourceAmount.compareTo(BigDecimal.ZERO) <= 0 || destAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Source and destination amounts must be positive");
        }

        if (appliedRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleViolationException("Applied rate must be positive");
        }


        BigDecimal expected = sourceAmount.multiply(appliedRate).setScale(4, RoundingMode.HALF_UP);
        BigDecimal actual = destAmount.setScale(4, RoundingMode.HALF_UP);

        // Sabit kuruş toleransı (Örn: Maksimum 0.05 birim sapma / yuvarlama farkı)
        BigDecimal maxTolerance = new BigDecimal("0.05");

        if (expected.subtract(actual).abs().compareTo(maxTolerance) > 0) {
            throw new BusinessRuleViolationException(
                    "Applied rate is not consistent with source and destination amounts. Expected: " + expected + ", Actual: " + actual
            );
        }

    }
    
    
}
