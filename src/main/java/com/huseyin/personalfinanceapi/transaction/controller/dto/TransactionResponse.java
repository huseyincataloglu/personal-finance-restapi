package com.huseyin.personalfinanceapi.transaction.controller.dto;

import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record TransactionResponse(
        Long id,
        TransactionType type,
        boolean active,
        boolean reversed,
        Long referenceTransactionId,
        String description,
        Instant dateAndTime,
        Long categoryId,
        BigDecimal appliedRate,
        List<EntryDto> entries,
        Instant createdAt
) {
    public record EntryDto(
            Long id,
            Long accountId,
            Entry.EntryType entryType,
            Entry.Direction direction,
            BigDecimal amount,
            String currencyCode,
            String assetSymbol,
            String assetUnit,
            BigDecimal quantity
    ) {}

    public static TransactionResponse from(Transaction t) {
        Long catId = (t instanceof UniDirectionalTransaction u && u.getCategory() != null)
                ? u.getCategory().getId() : null;
        BigDecimal rate = (t instanceof ExchangeTransaction ex) ? ex.getAppliedRate() : null;
        List<EntryDto> entries = t.getEntryList().stream()
                .map(TransactionResponse::toEntryDto)
                .toList();
        return new TransactionResponse(
                t.getId(), t.getType(), t.isActive(), t.isReversed(),
                t.getReversesTransactionId(), t.getDescription(), t.getTime(),
                catId, rate, entries, t.getCreatedAt());
    }

    private static EntryDto toEntryDto(Entry e) {
        if (e instanceof CashEntry c) {
            return new EntryDto(c.getId(), c.getAccount().getId(), c.getEntryType(),
                    c.getDirection(),
                    c.getAmount() != null ? c.getAmount().amount() : null,
                    c.getAmount() != null ? c.getAmount().currencyCode() : null,
                    null, null, null);
        }
        if (e instanceof AssetEntry a) {
            return new EntryDto(a.getId(), a.getAccount().getId(), a.getEntryType(),
                    a.getDirection(),
                    a.getUnitPrice() != null ? a.getUnitPrice().amount() : null,
                    a.getUnitPrice() != null ? a.getUnitPrice().currencyCode() : null,
                    a.getAssetSymbol(), a.getAssetUnit(), a.getQuantity());
        }
        return new EntryDto(e.getId(), e.getAccount().getId(), e.getEntryType(),
                e.getDirection(), null, null, null, null, null);
    }
}
