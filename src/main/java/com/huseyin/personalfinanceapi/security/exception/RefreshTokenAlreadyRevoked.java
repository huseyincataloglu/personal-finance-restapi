package com.huseyin.personalfinanceapi.security.exception;

public class RefreshTokenAlreadyRevoked extends RuntimeException {
    public RefreshTokenAlreadyRevoked(String message) {
        super(message);
    }
}
