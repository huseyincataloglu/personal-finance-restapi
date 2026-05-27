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
import com.huseyin.personalfinanceapi.transaction.processor.policy.AssetSellAccountPolicy;
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

        AssetSellAccountPolicy.validate(command.srcAssetAccount(),command.destCashAccount());

        AssetAccount srcAssetAccount  = (AssetAccount) command.srcAssetAccount();
        BalanceAccount destCashAccount = (BalanceAccount)command.destCashAccount();

        AssetAccountHolding holding;

        String assetCurrency;
        if(assetAccountHolding == null){
            throw new BusinessRuleViolationException("There is no holding record related with asset");
        }
        else {
            assetCurrency = command.asset().getCurrency();
        }

        Transaction transaction = new Transaction();
        transaction.setUser(command.user());
        transaction.setType(TransactionType.ASSET_SELL);
        transaction.setDescription(command.description() != null? command.description(): "Asset_sell transaction");
        transaction.setTime(command.dateAndTime());

        Money destTotalIncome = Money.of(calculateTotalSellIncome(command),assetCurrency);
        CashEntry destEntry = new CashEntry(destTotalIncome, Entry.Direction.INWARD);
        destEntry.setAccount(destCashAccount);


        return repository.save(transaction);

    }

    private static BigDecimal calculateTotalSellIncome(AssetSellCommand command) {
        return command.unitPriceAmount()
                .multiply(command.quantity())
                .subtract(command.otherFees() != null ? command.otherFees() : BigDecimal.ZERO);
    }


}
