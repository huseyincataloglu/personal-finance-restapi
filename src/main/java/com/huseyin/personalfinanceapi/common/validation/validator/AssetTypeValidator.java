package com.huseyin.personalfinanceapi.common.validation.validator;

import com.huseyin.personalfinanceapi.asset.entity.Asset;
import com.huseyin.personalfinanceapi.common.validation.annotation.ValidAssetType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AssetTypeValidator
        implements ConstraintValidator<
                ValidAssetType,
                String> { {


                }

    @Override
    public boolean isValid(
            String s, ConstraintValidatorContext constraintValidatorContext
    ) {
             if(s == null || s.isBlank()){
                 return false;
             }

             return Asset.AssetType.from(s) != null;

    }
}
