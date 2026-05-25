package com.huseyin.personalfinanceapi.common.validation.annotation;


import com.huseyin.personalfinanceapi.common.validation.validator.AssetTypeValidator;
import com.huseyin.personalfinanceapi.common.validation.validator.CurrencyCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({
        ElementType.FIELD,
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy =
        AssetTypeValidator.class)
public @interface ValidAssetType {

    String message()
            default "Invalid asset type";

    Class<?>[] groups()
            default {};

    Class<? extends Payload>[] payload()
            default {};
}
