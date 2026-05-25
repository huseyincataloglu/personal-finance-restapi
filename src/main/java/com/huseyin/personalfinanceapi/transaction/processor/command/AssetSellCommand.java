package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.user.entity.User;


import java.math.BigDecimal;
import java.time.Instant;

public record AssetSellCommand(
        User user,
        Account assetAccount,
        Account destinationAccount,
        String assetSymbol,
        String assetUnit,
        BigDecimal quantity,
        BigDecimal unitPriceAmount,
        BigDecimal otherFees,
        String description,
        Instant dateAndTime
) implements TransactionCommand {
}
