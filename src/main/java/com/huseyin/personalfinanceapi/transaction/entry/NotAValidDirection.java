package com.huseyin.personalfinanceapi.transaction.entry;

public class NotAValidDirection extends RuntimeException {
    public NotAValidDirection(String message) {
        super(message);
    }
}
