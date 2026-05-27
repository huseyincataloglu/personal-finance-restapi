package com.huseyin.personalfinanceapi.transaction.processor.command;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.user.entity.User;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record OpeningAssetCommand(
    User user,
    Account account,
    List<AssetOrder> assetOrders
) implements TransactionCommand {


    public record AssetOrder(
            Asset asset,
            List<AssetItem> assetItems
    ){

    }

   public record AssetItem(
           BigDecimal quantity,
           BigDecimal unitPrice
   ){


   }


}
