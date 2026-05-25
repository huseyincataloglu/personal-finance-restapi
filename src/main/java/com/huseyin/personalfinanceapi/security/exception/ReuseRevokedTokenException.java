package com.huseyin.personalfinanceapi.security.exception;

public class ReuseRevokedTokenException extends RuntimeException {
    public ReuseRevokedTokenException(String message) {
        super(message);
    }
}
