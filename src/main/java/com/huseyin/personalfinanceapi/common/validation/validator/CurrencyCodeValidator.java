package com.huseyin.personalfinanceapi.common.validation.validator;

import com.huseyin.personalfinanceapi.common.validation.annotation.ValidCurrency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;

public class CurrencyCodeValidator
        implements ConstraintValidator<
                ValidCurrency,
                String> {

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {

        if(value == null){
            return true;
        }

        if (value.isBlank()) {
            return false;
        }

        try {
            Currency.getInstance(
                    value.toUpperCase()
            );
            return true;
        } catch (
                IllegalArgumentException e
        ) {
            return false;
        }
    }
}
