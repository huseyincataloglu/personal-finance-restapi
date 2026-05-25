package com.huseyin.personalfinanceapi.transaction.resolver;

import com.huseyin.personalfinanceapi.account.AccountStatus;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.service.AccountService;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.service.TransactionService;
import org.springframework.stereotype.Service;

@Service
public class AccountAccessResolver {

    private final AccountService accountService;

    public AccountAccessResolver(AccountService accountService) {
        this.accountService = accountService;
    }

    public Account resolveOwnedAccount(
            Long accountId, Long userId
    ){
        Account a = accountService.getAccountForUser(accountId,userId);
        requireActive(a);
        return a;
    }

    // validates whether the account on which the transactşon is performed is active
    private void requireActive(Account account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessRuleViolationException(
                    "Pasif hesap üzerinde işlem yapılamaz: id=" + account.getId());
        }
    }



}
