package com.huseyin.personalfinanceapi.account.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message) {
        super(message);
    }
    public AccountNotFoundException(Long id) {
        super("Hesap bulunamadı: id=" + id);
    }
}
