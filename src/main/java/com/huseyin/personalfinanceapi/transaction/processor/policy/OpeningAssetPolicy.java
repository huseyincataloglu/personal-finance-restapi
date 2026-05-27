package com.huseyin.personalfinanceapi.transaction.processor.policy;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Component;

@Component
public class OpeningAssetPolicy {

    private final TransactionRepository transactionRepository;

    public OpeningAssetPolicy(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean isAccountAllowed(AccountType type) {
        return type.isAssetBased();
    }

    public void validate(Account account) {
        if(!(account instanceof AssetAccount)){
            throw new BusinessRuleViolationException("Opening-asset transaction needs asset accounts to perform.");
        }

        if(!isAccountAllowed(account.getType())){
            throw new BusinessRuleViolationException(
                    account.getType().name()+" account is not allowed for opening-asset."
            );
        }

        if(transactionRepository.existsByTypeAndReversedFalseAndEntryList_Account(TransactionType.OPENING_ASSET,account)){
            throw new BusinessRuleViolationException(
                    "Opening-asset can only be defined once."
            );
        }


    }



}
