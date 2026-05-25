package com.huseyin.personalfinanceapi.transaction.exception;

/**
 * Talimattaki katı muhasebe / iş kurallarına aykırı bir işlem talebi geldiğinde
 * fırlatılır (ör. kredi kartından kredi hesabına transfer, borç hesabıyla
 * varlık alımı, başlangıç bakiyesinin tekrar girilmesi vb.).
 */
public class BusinessRuleViolationException extends RuntimeException {
    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
