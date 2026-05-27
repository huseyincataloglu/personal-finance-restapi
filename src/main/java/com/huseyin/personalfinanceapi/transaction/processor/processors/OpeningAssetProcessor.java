package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.OpeningAssetCommand;
import com.huseyin.personalfinanceapi.transaction.processor.command.TransactionCommand;
import com.huseyin.personalfinanceapi.transaction.processor.policy.OpeningAssetPolicy;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpeningAssetProcessor implements TransactionProcessor<OpeningAssetCommand> {

    private final TransactionRepository transactionRepository;
    private final OpeningAssetPolicy openingAssetPolicy;
    private final TransactionEngine engine;


    @Override
    public TransactionType getSupportedType() {
        return TransactionType.OPENING_ASSET;
    }

    @Override
    public Transaction process(OpeningAssetCommand command) {

        List<OpeningAssetCommand.AssetOrder> orders = command.assetOrders();
        if(orders.isEmpty()){
            throw new BusinessRuleViolationException("At least one asset order must be included in opening-asset transactions");
        }
        boolean isAnyOrderHasNoItem = orders.stream().anyMatch(it -> it.assetItems().isEmpty());
        if(isAnyOrderHasNoItem){
            throw new BusinessRuleViolationException("quantities and unitprices must be send for related assets");
        }
        openingAssetPolicy.validate(command.account());
        AssetAccount account = (AssetAccount) command.account();

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setDescription("Asset Account Opening Assets");
        transaction.setType(TransactionType.OPENING_ASSET);
        transaction.setTime(Instant.from(account.getCreatedAt()));

        for (OpeningAssetCommand.AssetOrder order : orders) {
            for (OpeningAssetCommand.AssetItem item : order.assetItems()) {

                AssetEntry assetEntry = new AssetEntry();
                assetEntry.setAccount(account);
                assetEntry.setAsset(order.asset());
                assetEntry.setQuantity(item.quantity());
                assetEntry.setUnitPrice(Money.of(item.unitPrice(),order.asset().getCurrency()));
                assetEntry.setDirection(Entry.Direction.INWARD);

                transaction.addEntry(assetEntry);
            }
        }

        engine.applyEntries(transaction.getEntryList());
        return transactionRepository.save(transaction);

    }



}
