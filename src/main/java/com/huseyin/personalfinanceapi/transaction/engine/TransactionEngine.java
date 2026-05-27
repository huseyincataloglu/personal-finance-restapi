package com.huseyin.personalfinanceapi.transaction.engine;

import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.holding.AssetAccountHolding;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
@RequiredArgsConstructor
public class  TransactionEngine {


    public void applyEntries(List<Entry> entries) {
        entries.forEach(this::applyEntry);
    }

    private void applyEntry(Entry entry) {
        if (entry instanceof CashEntry cashEntry) {
            applyCashDelta(cashEntry);
        } else if (entry instanceof AssetEntry assetEntry) {
            applyAssetDelta(assetEntry);
        } else {
            throw new IllegalStateException("Unknown entry type: " + entry.getClass());
        }
    }


    // ---------------- CASH ----------------
    public void applyCashDelta(CashEntry cashEntry) {

        BalanceAccount account = (BalanceAccount) cashEntry.getAccount();
        Money current = account.getBalance();
        Money entryAmount = cashEntry.getAmount();
        Entry.Direction entryDirection = cashEntry.getDirection();

        Money next;
        if (entryDirection == Entry.Direction.OUTWARD) {
            next = current.subtract(entryAmount);
        } else {
            next = current.add(entryAmount);
        }
        account.setBalance(next);
    }

    // ---------------- ASSET ----------------

    public void applyAssetDelta(AssetEntry entry) {
        AssetAccount account = (AssetAccount) entry.getAccount();
        Asset asset = entry.getAsset();
        if(entry.getDirection() == Entry.Direction.INWARD){
            AssetAccountHolding holding;
            if(!(account.existsHolding(asset))){
                holding = account.initializeHolding(asset);
                holding.setQuantity(entry.getQuantity());
                holding.setTotalCost(entry.getUnitPrice().multiply(entry.getQuantity(),8).amount());
                holding.setAverageUnitPrice(entry.getUnitPrice().amount());

            }
            else {
                holding = account.findHolding(asset);

                BigDecimal newTotalQuantity = entry.getQuantity().add(holding.getQuantity());
                BigDecimal entryCost = entry.getUnitPrice().multiply(entry.getQuantity(),8).amount();
                BigDecimal newTotalCost = entryCost.add(holding.getTotalCost());
                BigDecimal newAverageUnitPrice = newTotalCost.divide(newTotalQuantity,8,RoundingMode.HALF_UP);

                holding.setQuantity(newTotalQuantity);
                holding.setTotalCost(newTotalCost);
                holding.setAverageUnitPrice(newAverageUnitPrice);

            }
            
        } else if (entry.getDirection() == Entry.Direction.OUTWARD) {
            AssetAccountHolding holding = account.findHolding(asset);// find holding

            BigDecimal newTotalQuantity = holding.getQuantity().subtract(entry.getQuantity());// calculate new subtracted quantity

            if (newTotalQuantity.compareTo(BigDecimal.ZERO) == 0) { // check if the remaining quantity is zero
                holding.setTotalCost(BigDecimal.ZERO);
                holding.setQuantity(BigDecimal.ZERO);
            } else {

                BigDecimal newTotalCost = newTotalQuantity.multiply(holding.getAverageUnitPrice()); // calculate new total cost
                // !! We dont recalculate weighted average unit price in asset selling transactions
                holding.setQuantity(newTotalQuantity);
                holding.setTotalCost(newTotalCost);
            }
        }


    }





}
