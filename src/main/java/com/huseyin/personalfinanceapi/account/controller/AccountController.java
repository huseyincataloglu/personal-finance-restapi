package com.huseyin.personalfinanceapi.account.controller;


import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.controller.dto.AccountFilter;
import com.huseyin.personalfinanceapi.account.controller.dto.AccountResponse;
import com.huseyin.personalfinanceapi.account.controller.dto.CreateAccountRequest;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.service.AccountService;
import com.huseyin.personalfinanceapi.common.PagedResponse;
import com.huseyin.personalfinanceapi.transaction.controller.dto.*;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.service.TransactionQueryService;
import com.huseyin.personalfinanceapi.transaction.service.TransactionService;
import com.huseyin.personalfinanceapi.common.TransactionType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Account aggregate'inin REST yüzeyi. Hesaplara ait CRUD'un yanı sıra,
 * <b>account-bağlamlı transaction yazma endpoint'leri</b> de burada bulunur:
 * çünkü bu işlemler her zaman bir hesabın bağlamında üretilir.
 *
 * <p>Transaction-merkezli okuma/sorgulama (cross-account listeleme, tek bir
 * işleme bakma, ters çevirme) için {@code /transactions} altındaki
 * {@code TransactionController} kullanılır.
 *
 * <h3>Endpoint haritası</h3>
 * <pre>
 *  GET    /accounts                                 → filtre + sayfalı liste
 *  GET    /accounts/{id}
 *  POST   /accounts
 *  GET    /accounts/{id}/transactions               → bu hesabın işlemleri (filtre)
 *  POST   /accounts/{id}/transactions/income
 *  POST   /accounts/{id}/transactions/expense
 *  POST   /accounts/{id}/transactions/transfer
 *  POST   /accounts/{id}/transactions/exchange
 *  POST   /accounts/{id}/transactions/asset-purchase
 *  POST   /accounts/{id}/transactions/asset-sell
 *  POST   /accounts/{id}/transactions/initial-balance
 * </pre>
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionQueryService transactionQueryService;

    public AccountController(AccountService accountService,
                             TransactionQueryService transactionQueryService) {
        this.accountService = accountService;
        this.transactionQueryService = transactionQueryService;
    }

    // -----------------------------------------------------------------------
    //  Account CRUD
    // -----------------------------------------------------------------------

    /**
     * Filtre + sayfalama ile hesap listesi. Filtre query param'lardan okunur:
     * {@code ?types=WALLET,BANK&currencyCode=TRY&active=true&nameContains=ana
     *        &balanceMin=100&balanceMax=10000&page=0&size=20&sort=name,asc}
     */
    @GetMapping
    public ResponseEntity<PagedResponse<AccountResponse>> list(
            @RequestParam(required = false) List<AccountType> types,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String nameContains,
            @RequestParam(required = false) BigDecimal balanceMin,
            @RequestParam(required = false) BigDecimal balanceMax,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal Long userId) {
        AccountFilter filter = new AccountFilter(types, currencyCode, active,
                nameContains, balanceMin, balanceMax);
        Page<Account> page = accountService.search(userId, filter, pageable);
        return ResponseEntity.ok(PagedResponse.of(page, AccountResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountForUser(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        Account account = accountService.getAccountForUser(id, userId);
        return ResponseEntity.ok(AccountResponse.from(account));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @AuthenticationPrincipal Long userId) {
        Account account = accountService.createAccount(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AccountResponse.from(account));
    }

    // -----------------------------------------------------------------------
    //  Account-bağlamlı transaction listesi (filtre + sayfalama)
    // -----------------------------------------------------------------------

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<PagedResponse<TransactionResponse>> listTransactions(
            @PathVariable Long accountId,
            @RequestParam(required = false) List<TransactionType> types,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) Entry.Direction direction,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Boolean reversed,
            @RequestParam(required = false) String assetSymbol,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal Long userId) {
        TransactionFilter filter = new TransactionFilter(
                types, null, categoryIds, direction, dateFrom, dateTo,
                amountMin, amountMax, currencyCode, search, active, assetSymbol);
        Page<Transaction> page = transactionQueryService.searchForAccount(
                userId, accountId, filter, pageable);
        return ResponseEntity.ok(PagedResponse.of(page, TransactionResponse::from));
    }



    private ResponseEntity<TransactionResponse> created(Transaction tx) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(tx));
    }
}
