package com.huseyin.personalfinanceapi.security.exception;

public class RefreshTokenNotExistsException extends RuntimeException {
    public RefreshTokenNotExistsException(String message) {
        super(message);
    }
}
