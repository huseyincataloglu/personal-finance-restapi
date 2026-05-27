package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.user.entity.User;


import java.math.BigDecimal;
import java.time.Instant;

public record AssetSellCommand(
        User user,
        Account srcAssetAccount,
        Account destCashAccount,
        Asset asset,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        String description,
        Instant dateAndTime
) implements TransactionCommand {
}
