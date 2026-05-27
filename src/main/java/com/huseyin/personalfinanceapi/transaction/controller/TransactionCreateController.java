package com.huseyin.personalfinanceapi.transaction.controller;

import com.huseyin.personalfinanceapi.transaction.controller.dto.*;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * This controller is responsible for creating transactions
 * Asset-included transactions are placed into AssetTransactionController
 */
@RestController
@RequestMapping("/transactions")
public class TransactionCreateController {

    private final TransactionService transactionService;

    public TransactionCreateController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

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


    private ResponseEntity<TransactionResponse> created(Transaction tx) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(tx));
    }




}
