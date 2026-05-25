package com.huseyin.personalfinanceapi.transaction.controller.dto;

import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Transaction listeleme/arama için filtre. Tüm alanlar opsiyonel; null ya da
 * boş ise o ölçüt uygulanmaz. Spring controller request param'larından
 * doldurulur ve {@code TransactionSpecifications.fromFilter()}'a aktarılır.
 *
 * @param types          birden fazla TransactionType (multi-value)
 * @param accountIds     işlemde herhangi bir entry'si bu hesap(lar)dan birinde
 * @param categoryIds    UniDirectionalTransaction.category id'leri
 * @param direction      INWARD veya OUTWARD — tek yönlü filtre
 * @param dateFrom       inclusive
 * @param dateTo         inclusive
 * @param amountMin      cash entry tutarı bu değerden büyük/eşit olan en az bir entry
 * @param amountMax      cash entry tutarı bu değerden küçük/eşit olan en az bir entry
 * @param currencyCode   cash entry para birimi
 * @param search         description içinde geçen ifade (case-insensitive)
 * @param reversed       null = filtre yok, true = orijinali terslenmiş olanlar
 * @param assetSymbol    asset entry symbol filtre (asset işlemleri için)
 */
public record TransactionFilter(
        List<TransactionType> types,
        List<Long> accountIds,
        List<Long> categoryIds,
        Entry.Direction direction,
        Instant dateFrom,
        Instant dateTo,
        BigDecimal amountMin,
        BigDecimal amountMax,
        String currencyCode,
        String search,
        Boolean reversed,
        String assetSymbol
) {
}
