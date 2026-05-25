package com.huseyin.personalfinanceapi.common;


import com.huseyin.personalfinanceapi.common.exception.*;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Para birimi farkındalıklı, ölçek-korumalı ve immutable Value Object.
 *
 * <p>JPA ile {@code @Embedded} olarak kullanılır. JPA no-arg constructor ihtiyacı için
 * protected default constructor sağlanır, fakat dış dünyaya immutable görünür.
 */
@Embeddable
public class Money {

    public static final int DEFAULT_SCALE = 4;
    public static final RoundingMode DEFAULT_ROUNDING = RoundingMode.HALF_UP;

    @Column(name = "amount", precision = 19, scale = 4, nullable = false)
    private final BigDecimal amount;

    @Column(name = "currency", length = 3, nullable = false)
    private final String currencyCode;

    protected Money() {
        this.amount = null;
        this.currencyCode = null;
    }

    private Money(BigDecimal amount, String currencyCode, int scale, RoundingMode rounding) {
        this.amount = amount.setScale(scale, rounding);
        try{
            Currency.getInstance(currencyCode);
        }
        catch (IllegalArgumentException ex){
            throw new BusinessRuleViolationException("currency: %s is not a valid ISO 4217 format".formatted(currencyCode));
        }
        this.currencyCode = currencyCode;
    }

    public static Money zero(String currencyCode) {
        return new Money(BigDecimal.ZERO, currencyCode,DEFAULT_SCALE,DEFAULT_ROUNDING);
    }

    /**
     * //For DB, user monitoring. Configures amount inside the money by setting scale 4 and rounding half up.
     * These are default scale and round values. We create money with these default settings when we get money outside.
     * We also store and show money to the clients with these default settings.
     * @param amount
     * @param currencyCode
     * @return
     */
    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, currencyCode, DEFAULT_SCALE, DEFAULT_ROUNDING);
    }

    /**
     * Creates Money object by obtaining scale from outside
     * @param amount
     * @param currencyCode
     * @param scale
     * @return
     */
    public static Money ofExact(BigDecimal amount, String currencyCode, int scale) {
        return new Money(amount, currencyCode, scale, RoundingMode.HALF_UP);
    }


    // Adding money to another money
    public Money add(Money other) {
        validateCurrency(other);
        if (!other.isGreaterThanZero()) {
            throw new MoneyAdditionException("Money addition amount must be greater than zero");
        }
        return new Money(
                this.amount.add(other.amount),
                this.currencyCode,
                this.amount.scale(),
                DEFAULT_ROUNDING
        );
    }

    // Subtracting money from another money
    public Money subtract(Money other) {
        validateCurrency(other);
        if (!other.isGreaterThanZero()) {
            throw new MoneySubstractionException("Subtracted money must be greater than zero");
        }

        return new Money(
                this.amount.subtract(other.amount),
                this.currencyCode,
                this.amount.scale(),
                DEFAULT_ROUNDING
                );
    }

    public Money multiply(BigDecimal multiplier) {
        // Gösterim için, scale korunur
        return new Money(
                this.amount.multiply(multiplier),
                this.currencyCode,
                DEFAULT_SCALE,
                DEFAULT_ROUNDING
        );
    }

    // Multiplying money with a scalar value
    public Money multiply(
            BigDecimal multiplier,
            int scale
    ){

        Objects.requireNonNull(
                multiplier,
                "multiplier cannot be null"
        );

        if(multiplier.compareTo(BigDecimal.ZERO)<= 0){
            throw new MoneyException("Scalar multiplier amount must be greater than zero.");
        }
        return new Money(
                this.amount.multiply(multiplier),
                this.currencyCode,
                scale,
                DEFAULT_ROUNDING
        );

    }
    // Dividing money with a scalar value
    public Money divide(
            BigDecimal divisor,
            int scale,
            RoundingMode mode
    ){

        Objects.requireNonNull(
                divisor,
                "divisor cannot be null"
        );

        if(divisor.compareTo(BigDecimal.ZERO)<= 0){
            throw new MoneyException("Scalar divisor amount must be greater than zero.");
        }

        BigDecimal newAmount = this.amount.divide(divisor,scale,mode);
        return Money.ofExact(newAmount,this.currencyCode,scale);
    }

    public Money normalize(){
        return Money.of(this.amount,this.currencyCode);
    }

    public boolean isLessThan(Money other) {
        validateCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isGreaterOrEqual(Money other) {
        validateCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }


    public boolean isGreaterThanZero() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }


    public boolean hasSameCurrencyAs(Money other) {
        return this.currencyCode.equals(other.currencyCode);
    }

    private void validateCurrency(Money other) {
        Objects.requireNonNull(other, "other null olamaz");
        if (!this.currencyCode.equals(other.currencyCode)) {
            throw new CurrencyMismatchException(
                    "Para birimleri uyuşmuyor: " + this.currencyCode + " vs " + other.currencyCode);
        }
    }

    public BigDecimal amount() {
        return amount;
    }

    public String currencyCode() {
        return currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money m)) return false;
        return amount.compareTo(m.amount) == 0 && currencyCode.equals(m.currencyCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currencyCode);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currencyCode;
    }
}
