package com.huseyin.personalfinanceapi.transaction.controller;

import com.huseyin.personalfinanceapi.transaction.controller.dto.TransactionResponse;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.service.TransactionQueryService;
import com.huseyin.personalfinanceapi.transaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("transactions")
public class TransactionAdjustmentController {

    private final TransactionService transactionService;

    public TransactionAdjustmentController(TransactionService transactionService) {
        this.transactionService = transactionService;
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


    private ResponseEntity<TransactionResponse> created(Transaction tx) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(tx));
    }








}
