package com.huseyin.personalfinanceapi.account.service;


import com.huseyin.personalfinanceapi.account.AccountStatus;
import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.controller.dto.AccountFilter;
import com.huseyin.personalfinanceapi.account.controller.dto.CreateAccountRequest;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.account.exception.AccountAccessDeniedException;
import com.huseyin.personalfinanceapi.account.exception.AccountNotFoundException;
import com.huseyin.personalfinanceapi.account.repository.AccountRepository;
import com.huseyin.personalfinanceapi.auth.exception.UserAccountDisabledException;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.accessgate.UserAccessGate;
import com.huseyin.personalfinanceapi.transaction.controller.dto.CreateInitialBalanceRequest;
import com.huseyin.personalfinanceapi.transaction.processor.command.InitialBalanceCommand;
import com.huseyin.personalfinanceapi.transaction.processor.processors.InitialBalanceProcessor;
import com.huseyin.personalfinanceapi.transaction.service.TransactionService;
import com.huseyin.personalfinanceapi.user.entity.User;
import com.huseyin.personalfinanceapi.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Account aggregate'inin oluşturma/sorgulama orkestrasyonu. Bakiye / holding
 * mutasyonu burada yapılmaz — bu işlemler yalnızca {@code TransactionService}
 * üzerinden, audit trail için Transaction kayıtları oluşturularak yürütülür.
 *
 * <p>Initial balance kaydı bu serviste değil, TransactionService'de tutulur.
 * Burada sadece "iskelet" hesap kurulur. Opsiyonel başlangıç bakiyesi
 * sağlanmışsa controller orchestrator katmanı bunu TransactionService'e iletir.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final InitialBalanceProcessor initialBalanceProcessor;
    private final UserAccessGate userAccessGate;
    private final UserRepository userRepository;

    public AccountService(AccountRepository accountRepository,
                          InitialBalanceProcessor initialBalanceProcessor,
                          UserAccessGate userAccessGate,
                          UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.initialBalanceProcessor = initialBalanceProcessor;
        this.userAccessGate = userAccessGate;
        this.userRepository = userRepository;
    }

    /** Filtre + sayfalama ile kullanıcının hesaplarını sorgular. */
    public Page<Account> search(Long userId, AccountFilter filter, Pageable pageable) {
        Specification<Account> spec = AccountSpecifications.fromFilter(userId, filter);
        return accountRepository.findAll(spec, pageable);
    }

    public Account getAccountForUser(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserId(accountId,userId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Transactional
    public Account createAccount(Long userId, CreateAccountRequest request) {

        User owner = userAccessGate.requireFinancialAccess(userId);

        AccountType type = request.type();

        Account account;
        if (type.isAssetBased()) {
            account = new AssetAccount();
        } else {
            BalanceAccount balance = new BalanceAccount();
            balance.setBalance(Money.zero(request.currencyCode()));
            account = balance;
        }
        account.setUser(owner);
        account.setName(request.name());
        account.setType(type);
        account.setStatus(AccountStatus.ACTIVE);

        return accountRepository.save(account); // to ensure that any error has not happneded due to constraints

    }

    /**
     * Yeni kayıt olan kullanıcı için varsayılan TRY cinsinden bir Cüzdan hesabı
     * oluşturur. İdempotent — aynı kullanıcı için zaten WALLET varsa null döner.
     */
    @Transactional
    public Account createDefaultCashWallet(User user) {
        if (accountRepository.existsByUserIdAndType(user.getId(), AccountType.WALLET)) {
            return null;
        }
        BalanceAccount wallet = new BalanceAccount();
        wallet.setUser(user);
        wallet.setName("Wallet");
        wallet.setType(AccountType.WALLET);
        wallet.setStatus(AccountStatus.ACTIVE);
        wallet.setBalance(Money.zero(user.getBaseCurrency()));
        wallet.setDefault(true); // Setting it default
        return accountRepository.save(wallet);
    }

    @Transactional
    public void save(Account account){
         accountRepository.save(account);
    }


}
