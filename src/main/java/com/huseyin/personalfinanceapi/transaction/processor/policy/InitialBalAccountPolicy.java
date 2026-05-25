package com.huseyin.personalfinanceapi.transaction.processor.policy;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class InitialBalAccountPolicy {
    private final TransactionRepository transactionRepository;

    public InitialBalAccountPolicy(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean isAccountAllowed(AccountType type) {
        return !type.isAssetBased();
    }

    public void validate(Account account) {
        if(!(account instanceof BalanceAccount)){
            throw new BusinessRuleViolationException("Initial Balance transaction needs balance to perform.");
        }

        if(!isAccountAllowed(account.getType())){
            throw new BusinessRuleViolationException(
                    account.getType().name()+" account is not allowed for Initial Balance."
            );
        }

        if(transactionRepository.existsNonReversedInitialBalanceForAccount(account.getId(),TransactionType.INITIAL_BALANCE)){
            throw new BusinessRuleViolationException(
                    "Inıtial balance can be performed only once."
            );
        }

        if (transactionRepository.existsNonReversedNonInitialBalanceForAccount(account.getId(),TransactionType.INITIAL_BALANCE)) {
            throw new BusinessRuleViolationException(
                    "To an account, initial balance must only be first transaction");
        }
    }
}
