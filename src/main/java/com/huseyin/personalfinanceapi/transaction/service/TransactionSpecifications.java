package com.huseyin.personalfinanceapi.transaction.service;

import com.huseyin.personalfinanceapi.transaction.controller.dto.TransactionFilter;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaction üzerinde dinamik filtreler. Birden çok specification AND ile
 * birleştirilir. Karmaşık entry-level kriterler (tutar, currency, direction,
 * accountIds, assetSymbol) <em>EXISTS subquery</em> ile uygulanır — root
 * sorgunun {@code DISTINCT} ihtiyacını ortadan kaldırır ve sayfalamayı
 * (Pageable) güvenli hale getirir.
 */
public final class TransactionSpecifications {

    private TransactionSpecifications() {}

    public static Specification<Transaction> ownedBy(Long userId) {
        return (root, q, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Transaction> typeIn(List<?> types) {
        return (root, q, cb) -> {
            if (types == null || types.isEmpty()) return cb.conjunction();
            return root.get("type").in(types);
        };
    }

    public static Specification<Transaction> dateBetween(java.time.Instant from, java.time.Instant to) {
        return (root, q, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (from != null) ps.add(cb.greaterThanOrEqualTo(root.get("dateAndTime"), from));
            if (to != null) ps.add(cb.lessThanOrEqualTo(root.get("dateAndTime"), to));
            return ps.isEmpty() ? cb.conjunction() : cb.and(ps.toArray(Predicate[]::new));
        };
    }

    public static Specification<Transaction> activeIs(Boolean active) {
        return (root, q, cb) -> active == null ? cb.conjunction()
                : cb.equal(root.get("active"), active);
    }

    public static Specification<Transaction> reversedIs(Boolean reversed) {
        return (root, q, cb) -> reversed == null ? cb.conjunction()
                : cb.equal(root.get("reversed"), reversed);
    }

    public static Specification<Transaction> descriptionContains(String text) {
        return (root, q, cb) -> {
            if (text == null || text.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("description")), "%" + text.trim().toLowerCase() + "%");
        };
    }

    public static Specification<Transaction> categoryIn(List<Long> categoryIds) {
        return (root, q, cb) -> {
            if (categoryIds == null || categoryIds.isEmpty()) return cb.conjunction();
            // category yalnızca UniDirectionalTransaction üzerinde — TREAT ile cast.
            var unitx = cb.treat(root, Transaction.class);
            return unitx.get("category").get("id").in(categoryIds);
        };
    }

    /** Belirtilen hesaplardan birinde entry'si olan transaction'lar (EXISTS). */
    public static Specification<Transaction> hasEntryInAccounts(List<Long> accountIds) {
        return (root, q, cb) -> {
            if (accountIds == null || accountIds.isEmpty()) return cb.conjunction();
            Subquery<Long> sub = q.subquery(Long.class);
            var e = sub.from(Entry.class);
            sub.select(e.get("id"))
                    .where(cb.and(
                            cb.equal(e.get("transaction"), root),
                            e.get("account").get("id").in(accountIds)
                    ));
            return cb.exists(sub);
        };
    }

    /** Belirtilen direction'da en az bir entry'si olan transaction'lar. */
    public static Specification<Transaction> hasEntryWithDirection(Entry.Direction direction) {
        return (root, q, cb) -> {
            if (direction == null) return cb.conjunction();
            Subquery<Long> sub = q.subquery(Long.class);
            var e = sub.from(Entry.class);
            sub.select(e.get("id"))
                    .where(cb.and(
                            cb.equal(e.get("transaction"), root),
                            cb.equal(e.get("direction"), direction)
                    ));
            return cb.exists(sub);
        };
    }

    /**
     * Cash entry tutarı verilen aralıkta olan transaction'lar. amountMin/Max
     * null verilmişse o sınır uygulanmaz.
     */
    public static Specification<Transaction> hasCashAmountBetween(
            java.math.BigDecimal min, java.math.BigDecimal max, String currencyCode) {
        return (root, q, cb) -> {
            if (min == null && max == null && (currencyCode == null || currencyCode.isBlank())) {
                return cb.conjunction();
            }
            Subquery<Long> sub = q.subquery(Long.class);
            var ce = sub.from(CashEntry.class);
            List<Predicate> ps = new ArrayList<>();
            ps.add(cb.equal(ce.get("transaction"), root));
            if (min != null) ps.add(cb.greaterThanOrEqualTo(ce.get("amount").get("amount"), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(ce.get("amount").get("amount"), max));
            if (currencyCode != null && !currencyCode.isBlank()) {
                ps.add(cb.equal(ce.get("amount").get("currencyCode"), currencyCode));
            }
            sub.select(ce.get("id")).where(ps.toArray(Predicate[]::new));
            return cb.exists(sub);
        };
    }

    public static Specification<Transaction> hasAssetSymbol(String symbol) {
        return (root, q, cb) -> {
            if (symbol == null || symbol.isBlank()) return cb.conjunction();
            Subquery<Long> sub = q.subquery(Long.class);
            var ae = sub.from(AssetEntry.class);
            sub.select(ae.get("id"))
                    .where(cb.and(
                            cb.equal(ae.get("transaction"), root),
                            cb.equal(cb.lower(ae.get("assetSymbol")), symbol.toLowerCase())
                    ));
            return cb.exists(sub);
        };
    }

    /**
     * Bir TransactionFilter'ı, owner kısıtı ile birlikte tek bir Specification'a
     * derler. Filtre alanları null/boş ise o ölçüt no-op olur.
     */
    public static Specification<Transaction> fromFilter(Long userId, TransactionFilter f) {
        Specification<Transaction> spec = ownedBy(userId);
        if (f == null) return spec;

        return spec
                .and(typeIn(f.types()))
                .and(dateBetween(f.dateFrom(), f.dateTo()))
                .and(reversedIs(f.reversed()))
                .and(descriptionContains(f.search()))
                .and(categoryIn(f.categoryIds()))
                .and(hasEntryInAccounts(f.accountIds()))
                .and(hasEntryWithDirection(f.direction()))
                .and(hasCashAmountBetween(f.amountMin(), f.amountMax(), f.currencyCode()))
                .and(hasAssetSymbol(f.assetSymbol()));
    }

    /** Alias — tek bir hesabın işlemlerini filtrelerken pratik shortcut. */
    @SuppressWarnings("unchecked")
    public static Specification<Transaction> hasEntryInAccount(Long accountId) {
        return hasEntryInAccounts(accountId == null ? List.of() : List.of(accountId));
    }

    /** Aşağıdaki alanı kullanmamak için JoinType import'unu suppress eder. */
    private static void unusedJoinType() {
        JoinType ignored = JoinType.LEFT;
    }
}
