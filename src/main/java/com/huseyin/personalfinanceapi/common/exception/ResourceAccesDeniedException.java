package com.huseyin.personalfinanceapi.common.exception;

public class ResourceAccesDeniedException extends RuntimeException {
    public ResourceAccesDeniedException(String message) {
        super(message);
    }
}
