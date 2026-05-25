package com.huseyin.personalfinanceapi.transaction.engine;

import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.holding.AssetAccountHolding;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
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
            applyCashDelta((BalanceAccount) cashEntry.getAccount(), cashEntry);
        } else if (entry instanceof AssetEntry assetEntry) {
            applyAssetEntry((AssetAccount)assetEntry.getAccount(),assetEntry);
        } else {
            throw new IllegalStateException("Unknown entry type: " + entry.getClass());
        }
    }


    private void applyAssetEntry(AssetAccount assetAccount,AssetEntry assetEntry){
        if(assetEntry.getDirection() == Entry.Direction.INWARD){
            applyAssetPurchase(assetAccount,assetEntry);
        }
        else {
            applyAssetSell(assetAccount,assetEntry);
        }
    }


    // ---------------- CASH ----------------
    public void applyCashDelta(BalanceAccount account, CashEntry cashEntry) {

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

    public void applyAssetPurchase(AssetAccount account, AssetEntry assetEntry) {

        AssetAccountHolding holding =
                account.findHolding(assetEntry.getAssetSymbol(), assetEntry.getAssetUnit());

        if (assetEntry.getQuantity() == null ||assetEntry.getQuantity().signum() <= 0) {
            throw new IllegalArgumentException("Asset quantity needs to be positive to be purchased");
        }

        if (holding == null) {
            AssetAccountHolding createdHolding = createInitialAsset(account, assetEntry);
            account.addHolding(createdHolding);
        }
        else {
            applyPurchase(holding,assetEntry.getQuantity(),assetEntry.getUnitPrice());
        }

    }

    private void applyPurchase(AssetAccountHolding holding, BigDecimal quantity,Money unitPrice){
        // Calculate the asset's current total cost.
        Money totalCost = evaluateHoldingTotalPrice(holding);

        // Calculate added asset's total cost
        Money newCost = unitPrice.multiply(quantity);
        // calculate total quantity at the end of the addition
        BigDecimal totalQty = holding.getQuantity().add(quantity);

        Money newAverageUnitPrice = totalCost.add(newCost).divide(totalQty,8, RoundingMode.HALF_UP);
        holding.setQuantity(totalQty);
        holding.setAverageUnitPrice(newAverageUnitPrice);

    }

    private static Money evaluateHoldingTotalPrice(AssetAccountHolding holding) {
        return holding.getAverageUnitPrice().multiply(holding.getQuantity());
    }

    private AssetAccountHolding createInitialAsset(AssetAccount account, AssetEntry assetEntry) {
        AssetAccountHolding holding = new AssetAccountHolding();
        holding.setAssetSymbol(assetEntry.getAssetSymbol());
        holding.setAssetUnit(assetEntry.getAssetUnit());
        holding.setAccount(account);
        holding.setQuantity(assetEntry.getQuantity());
        holding.setAverageUnitPrice(assetEntry.getUnitPrice());
        return holding;
    }

    public void applyAssetSell(AssetAccount account, AssetAccountHolding holding, BigDecimal soldQuantity) {
        if (soldQuantity == null || soldQuantity.signum() <= 0) {
            throw new BusinessRuleViolationException("Sell amount must be positive");
        }

        BigDecimal holdingQuantity = holding.getQuantity();
        if (holdingQuantity == null || holdingQuantity.compareTo(soldQuantity) < 0) {
            throw new IllegalStateException("Insufficient asset quantity: " + holdingQuantity);
        }

        BigDecimal newQuantity = holdingQuantity.subtract(soldQuantity);

        if (newQuantity.signum() == 0) {
            // orphanRemoval = true olduğu için DB'den DELETE sorgusu atılacaktır.
            account.removeHolding(holding);
        } else {
            // Dirty checking sayesinde DB'ye UPDATE sorgusu atılacaktır.
            holding.setQuantity(newQuantity);
        }

    }

}
