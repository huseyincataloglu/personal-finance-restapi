package com.huseyin.personalfinanceapi.transaction.controller;

import com.huseyin.personalfinanceapi.common.PagedResponse;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.controller.dto.*;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.service.TransactionQueryService;
import com.huseyin.personalfinanceapi.transaction.service.TransactionService;
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
 * Transaction-merkezli REST yüzeyi. Bir işlemi <b>oluşturmak</b> her zaman
 * bir hesabın bağlamında yapıldığı için yazma endpoint'leri
 * {@code AccountController} altındadır. Burası işlemler arasında dolaşmak ve
 * tek tek işlemleri yönetmek için tasarlanmıştır.
 *
 * <h3>Endpoint haritası</h3>
 * <pre>
 *  GET    /transactions                → kullanıcının tüm işlemleri (filtre + sayfalama)
 *  GET    /transactions/{id}           → tek bir işlemi getir
 *  POST   /transactions/{id}/reverse   → işlemi tersle (REVERSE üretir)
 * </pre>
 *
 * <h3>Filtre parametreleri (hepsi opsiyonel)</h3>
 * <ul>
 *   <li>{@code types}        — multi: INCOME,EXPENSE,TRANSFER,EXCHANGE,ASSET_PURCHASE,ASSET_SELL,INITIAL_BALANCE,REVERSE</li>
 *   <li>{@code accountIds}   — multi: bir veya daha fazla hesap üzerinden filtre</li>
 *   <li>{@code categoryIds}  — multi: kategori id'leri (UniDirectional için)</li>
 *   <li>{@code direction}    — INWARD veya OUTWARD</li>
 *   <li>{@code dateFrom}, {@code dateTo} — ISO-8601 Instant aralığı</li>
 *   <li>{@code amountMin}, {@code amountMax} — cash entry tutarı aralığı</li>
 *   <li>{@code currencyCode} — cash entry para birimi (ör. TRY)</li>
 *   <li>{@code search}       — description metin araması (case-insensitive)</li>
 *   <li>{@code active}       — true/false</li>
 *   <li>{@code reversed}     — true/false (orijinali terslenmiş olanlar)</li>
 *   <li>{@code assetSymbol}  — asset entry sembolü (ör. AAPL, GOLD)</li>
 *   <li>{@code page}, {@code size}, {@code sort} — Pageable</li>
 * </ul>
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionQueryService queryService;

    public TransactionController(TransactionService transactionService,
                                 TransactionQueryService queryService) {
        this.transactionService = transactionService;
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TransactionResponse>> search(
            @RequestParam(required = false) List<TransactionType> types,
            @RequestParam(required = false) List<Long> accountIds,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) Entry.Direction direction,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(required = false) BigDecimal amountMin,
            @RequestParam(required = false) BigDecimal amountMax,
            @RequestParam(required = false) String currencyCode,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean reversed,
            @RequestParam(required = false) String assetSymbol,
            @PageableDefault(size = 20, sort = "dateAndTime") Pageable pageable,
            @AuthenticationPrincipal Long userId) {
        TransactionFilter filter = new TransactionFilter(
                types, accountIds, categoryIds, direction, dateFrom, dateTo,
                amountMin, amountMax, currencyCode, search, reversed, assetSymbol);
        Page<Transaction> page = queryService.search(userId, filter, pageable);
        return ResponseEntity.ok(PagedResponse.of(page, TransactionResponse::from));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> get(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        Transaction tx = queryService.getForUser(id, userId);
        return ResponseEntity.ok(TransactionResponse.from(tx));
    }


    //POST /opening-assets

    @PostMapping("/initial-balance")
    public ResponseEntity<TransactionResponse> initialBalance(
            @Valid @RequestBody CreateInitialBalanceRequest request,
            @AuthenticationPrincipal Long userId
    ){
        return created(transactionService.recordInitialBalance(userId,request));
    }

    @PostMapping("/income")
    public ResponseEntity<TransactionResponse> income(
            @Valid @RequestBody CreateIncomeRequest request,
            @AuthenticationPrincipal Long userId
    ){
        return created(transactionService.recordIncome(userId,request));
    }
    @PostMapping("expense")
    public ResponseEntity<TransactionResponse> expense(
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal Long userId
    ){
        return created(transactionService.recordExpense(userId,request));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody CreateTransferRequest request,
            @AuthenticationPrincipal Long userId) {
        return created(transactionService.recordTransfer(userId, request));
    }

    @PostMapping("/exchange")
    public ResponseEntity<TransactionResponse> exchange(
            @Valid @RequestBody CreateExchangeRequest request,
            @AuthenticationPrincipal Long userId) {
        return created(transactionService.recordExchange(userId, request));
    }

    @PostMapping("/asset-purchase")
    public ResponseEntity<TransactionResponse> assetPurchase(
            @Valid @RequestBody CreateAssetPurchaseRequest request,
            @AuthenticationPrincipal Long userId) {
        return created(transactionService.recordAssetPurchase(userId, request));
    }

    @PostMapping("/asset-sell")
    public ResponseEntity<TransactionResponse> assetSell(
            @Valid @RequestBody CreateAssetSellRequest request,
            @AuthenticationPrincipal Long userId) {
        return created(transactionService.recordAssetSell(userId, request));
    }

    /**
     * Bir işlemi tersler. Talimat gereği orijinal silinmez; yön-tersi entry'leri
     * olan yeni bir REVERSE işlemi üretilir, orijinal {@code reversed=true} ve
     * her ikisi de {@code active=false} işaretlenir. Aynı işlem ikinci kez
     * terslenemez.
     */
    @PostMapping("/{id}/reverse")
    public ResponseEntity<TransactionResponse> reverse(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        Transaction tx = transactionService.reverse(userId, id);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TransactionResponse.from(tx));
    }

    // generates revers transaction and both original and reverse one become inactive


    //Patch method to update some fields of the transactions -but not all of them



    private ResponseEntity<TransactionResponse> created(Transaction tx) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(tx));
    }



}
