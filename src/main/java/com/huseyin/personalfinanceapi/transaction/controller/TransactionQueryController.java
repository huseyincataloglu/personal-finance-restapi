package com.huseyin.personalfinanceapi.transaction.controller;


import com.huseyin.personalfinanceapi.common.PagedResponse;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.controller.dto.TransactionFilter;
import com.huseyin.personalfinanceapi.transaction.controller.dto.TransactionResponse;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.service.TransactionQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * This transaction is only responsible for searching, querying transaction resources
 */
@RestController
@RequestMapping("/transactions")
public class TransactionQueryController {

    private final TransactionQueryService queryService;

    public TransactionQueryController(TransactionQueryService queryService) {
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



}
