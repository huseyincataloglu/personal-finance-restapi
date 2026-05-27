package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.AssetPurchaseCommand;
import com.huseyin.personalfinanceapi.transaction.processor.policy.AssetPurchasePolicy;
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

        AssetPurchasePolicy.validate(command.sourceAccount(), command.destinationAccount());

        BalanceAccount srcCashAccount  =  (BalanceAccount) command.sourceAccount();
        AssetAccount destAssetAccount = (AssetAccount) command.destinationAccount();


        //Calculate total cash entry money
        BigDecimal totalCost = command.unitPriceAmount().multiply(command.quantity());

        Money cashEntryAmount = Money.of(totalCost,srcCashAccount.getBalance().currencyCode());

        Money assetUnitPrice = Money.of(command.unitPriceAmount(),command.asset().getCurrency());
        if(!cashEntryAmount.hasSameCurrencyAs(assetUnitPrice)){
            throw new BusinessRuleViolationException("Cash account currency does not match with asset's currency");
        }

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.ASSET_PURCHASE);
        transaction.setDescription(command.description() != null? command.description(): "Asset_purchase transaction");
        transaction.setTime(command.dateAndTime());

        CashEntry sourceEntry = new CashEntry(cashEntryAmount, Entry.Direction.OUTWARD);
        sourceEntry.setAccount(srcCashAccount);

        AssetEntry destinationEntry = new AssetEntry(
                command.asset(),
                command.quantity(),
                assetUnitPrice,
                Entry.Direction.INWARD
        );
        destinationEntry.setAccount(destAssetAccount);

        transaction.addEntry(sourceEntry);
        transaction.addEntry(destinationEntry);

        engine.applyEntries(transaction.getEntryList());
        return repository.save(transaction);

    }


}
