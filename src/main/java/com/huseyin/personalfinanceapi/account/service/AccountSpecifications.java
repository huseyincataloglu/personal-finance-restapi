package com.huseyin.personalfinanceapi.account.service;

import com.huseyin.personalfinanceapi.account.AccountType;
import com.huseyin.personalfinanceapi.account.controller.dto.AccountFilter;
import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Account üzerinde dinamik filtreler. Bakiye filtreleri yalnızca
 * BalanceAccount alt sınıfına {@code TREAT AS} ile uygulanır; AssetAccount'lar
 * bakiye filtresi varsa otomatik kapsam dışı kalır.
 */
public final class AccountSpecifications {

    private AccountSpecifications() {}

    public static Specification<Account> ownedBy(Long userId) {
        return (root, q, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Account> typeIn(List<AccountType> types) {
        return (root, q, cb) -> {
            if (types == null || types.isEmpty()) return cb.conjunction();
            return root.get("type").in(types);
        };
    }

    public static Specification<Account> activeIs(Boolean active) {
        return (root, q, cb) -> active == null ? cb.conjunction()
                : cb.equal(root.get("active"), active);
    }

    public static Specification<Account> currencyEquals(String currencyCode) {
        return (root, q, cb) -> {
            if (currencyCode == null || currencyCode.isBlank()) return cb.conjunction();
            return cb.equal(root.get("currencyCode"), currencyCode);
        };
    }

    public static Specification<Account> nameContains(String text) {
        return (root, q, cb) -> {
            if (text == null || text.isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + text.trim().toLowerCase() + "%");
        };
    }

    /**
     * Sadece BalanceAccount kayıtlarına bakiye sınırı uygular. Kullanıcı bakiye
     * filtresi verdiyse AssetAccount'lar otomatik elenir.
     */
    public static Specification<Account> balanceBetween(BigDecimal min, BigDecimal max) {
        return (root, q, cb) -> {
            if (min == null && max == null) return cb.conjunction();
            var ba = cb.treat(root, BalanceAccount.class);
            List<Predicate> ps = new ArrayList<>();
            if (min != null) ps.add(cb.greaterThanOrEqualTo(ba.get("balance").get("amount"), min));
            if (max != null) ps.add(cb.lessThanOrEqualTo(ba.get("balance").get("amount"), max));
            return cb.and(ps.toArray(Predicate[]::new));
        };
    }

    public static Specification<Account> fromFilter(Long userId, AccountFilter f) {
        Specification<Account> spec = ownedBy(userId);
        if (f == null) return spec;
        return spec
                .and(typeIn(f.types()))
                .and(currencyEquals(f.currencyCode()))
                .and(activeIs(f.active()))
                .and(nameContains(f.nameContains()))
                .and(balanceBetween(f.balanceMin(), f.balanceMax()));
    }
}
