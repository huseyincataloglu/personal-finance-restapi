package com.huseyin.personalfinanceapi.transaction.controller;

import com.huseyin.personalfinanceapi.transaction.controller.dto.asset.CreateAssetPurchaseRequest;
import com.huseyin.personalfinanceapi.transaction.controller.dto.asset.CreateAssetSellRequest;
import com.huseyin.personalfinanceapi.transaction.controller.dto.asset.OpeningAssetRequest;
import com.huseyin.personalfinanceapi.transaction.controller.dto.TransactionResponse;
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

@RestController
@RequestMapping("transactions")
public class AssetTransactionController {

    private final TransactionService transactionService;

    public AssetTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/opening-asset")
    public ResponseEntity<TransactionResponse> openingAsset(
            @Valid @RequestBody OpeningAssetRequest request,
            @AuthenticationPrincipal Long userId) {
        return created(transactionService.recordOpeningAsset(userId, request));
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

    private ResponseEntity<TransactionResponse> created(Transaction tx) {
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(tx));
    }



}
