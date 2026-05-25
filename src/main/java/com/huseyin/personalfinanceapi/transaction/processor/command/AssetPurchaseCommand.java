package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record AssetPurchaseCommand(
    User user,
    Account sourceAccount,
    Account destinationAccount,
    Asset asset,
    BigDecimal quantity,
    BigDecimal unitPriceAmount,
    String description,
    Instant dateAndTime
) implements TransactionCommand {
}
