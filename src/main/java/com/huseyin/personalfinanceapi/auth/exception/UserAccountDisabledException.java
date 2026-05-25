package com.huseyin.personalfinanceapi.auth.exception;

public class UserAccountDisabledException extends RuntimeException {
    public UserAccountDisabledException(String message) {
        super(message);
    }
}
