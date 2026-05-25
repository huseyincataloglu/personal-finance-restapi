package com.huseyin.personalfinanceapi.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long id) {
        super("İşlem bulunamadı: id=" + id);
    }
}
