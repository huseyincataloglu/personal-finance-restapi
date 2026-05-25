package com.huseyin.personalfinanceapi.transaction.exception;

public class MissingBaseCurrencyException extends RuntimeException {
    public MissingBaseCurrencyException(String message) {
        super(message);
    }
}
