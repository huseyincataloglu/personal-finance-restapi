package com.huseyin.personalfinanceapi.asset.exception;

public class InvalidAssetTypeException extends RuntimeException {
    public InvalidAssetTypeException(String message) {
        super(message);
    }
}
