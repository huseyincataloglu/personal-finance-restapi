package com.huseyin.personalfinanceapi.user.exception;

public class ValidTokenUserNotExistException extends RuntimeException {
    public ValidTokenUserNotExistException(String message) {
        super(message);
    }
}
