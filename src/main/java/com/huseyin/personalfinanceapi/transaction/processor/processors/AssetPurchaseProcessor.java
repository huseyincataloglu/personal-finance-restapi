package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.processor.command.AssetPurchaseCommand;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
public class AssetPurchaseProcessor implements TransactionProcessor<AssetPurchaseCommand> {

    private final TransactionEngine engine;
    private final TransactionRepository repository;

    @Override
    public TransactionType getSupportedType() {
        return TransactionType.ASSET_PURCHASE;
    }

    @Override
    public Transaction process(AssetPurchaseCommand command) {
        BalanceAccount sourceAccount  =  command.sourceAccount();
        AssetAccount destination = command.destinationAccount();

        //Calculate total cash entry money
        BigDecimal totalCost = command.unitPriceAmount()
                .multiply(command.quantity())
                .add(command.otherFees());

        Money sourceAmount = Money.of(totalCost,sourceAccount.getBalance().currencyCode());

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.ASSET_PURCHASE);
        transaction.setDescription(command.description() != null? command.description(): "Asset_purchase transaction");
        transaction.setTime(command.dateAndTime());

        CashEntry sourceEntry = new CashEntry(sourceAmount, Entry.Direction.OUTWARD);
        sourceEntry.setAccount(sourceAccount);

        Money unitPrice = Money.of(command.unitPriceAmount(),sourceAccount.getBalance().currencyCode());

        AssetEntry destinationEntry = new AssetEntry(command.assetSymbol(),
                command.assetUnit(),command.quantity(),unitPrice,Entry.Direction.INWARD);
        destinationEntry.setAccount(destination);

        transaction.addEntry(sourceEntry);
        transaction.addEntry(destinationEntry);

        engine.applyCashDelta(sourceAccount,sourceEntry);
        engine.applyAssetPurchase(destination, destinationEntry);
        return repository.save(transaction);

    }


}
