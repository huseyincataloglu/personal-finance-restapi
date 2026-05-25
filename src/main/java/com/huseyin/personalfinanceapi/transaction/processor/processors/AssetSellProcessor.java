package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.holding.AssetAccountHolding;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.AssetSellCommand;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AssetSellProcessor implements TransactionProcessor<AssetSellCommand> {

    private final TransactionEngine engine;
    private final TransactionRepository repository;

    @Override
    public TransactionType getSupportedType() {
        return TransactionType.ASSET_SELL;
    }

    @Override
    public Transaction process(AssetSellCommand command) {
        AssetAccount sourceAccount  =  command.assetAccount();
        BalanceAccount destinationAccount = command.destinationAccount();

        AssetAccountHolding assetAccountHolding = sourceAccount.findHolding(command.assetSymbol(),command.assetUnit());

        String assetCurrency;
        if(assetAccountHolding == null){
            throw new BusinessRuleViolationException("Holding does not exist");
        }
        else {
            assetCurrency = assetAccountHolding.getAverageUnitPrice().currencyCode();
        }

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.ASSET_SELL);
        transaction.setDescription(command.description() != null? command.description(): "Asset_sell transaction");
        transaction.setTime(command.dateAndTime());

        Money destTotalIncome = Money.of(calculateTotalSellIncome(command),assetCurrency);
        CashEntry destEntry = new CashEntry(destTotalIncome, Entry.Direction.INWARD);
        destEntry.setAccount(destinationAccount);

        Money unitPrice = Money.of(command.unitPriceAmount(),assetCurrency);
        AssetEntry sourceEntry = new AssetEntry(command.assetSymbol(),
                command.assetUnit(),command.quantity(),unitPrice,Entry.Direction.OUTWARD);
        sourceEntry.setAccount(sourceAccount);

        transaction.addEntry(sourceEntry);
        transaction.addEntry(destEntry);

        engine.applyCashDelta(destinationAccount,destEntry);
        engine.applyAssetSell(sourceAccount,assetAccountHolding,sourceEntry.getQuantity());
        return repository.save(transaction);

    }

    private static BigDecimal calculateTotalSellIncome(AssetSellCommand command) {
        return command.unitPriceAmount()
                .multiply(command.quantity())
                .subtract(command.otherFees() != null ? command.otherFees() : BigDecimal.ZERO);
    }


}
