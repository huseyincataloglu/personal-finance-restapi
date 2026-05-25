package com.huseyin.personalfinanceapi.common.globalexhandler;


import com.huseyin.personalfinanceapi.account.exception.AccountAccessDeniedException;
import com.huseyin.personalfinanceapi.account.exception.AccountNotFoundException;
import com.huseyin.personalfinanceapi.account.exception.InvalidAccountTypeException;
import com.huseyin.personalfinanceapi.category.exception.BuiltInCategoryModificationException;
import com.huseyin.personalfinanceapi.category.exception.CategoryAccessDeniedException;
import com.huseyin.personalfinanceapi.category.exception.CategoryInUseException;
import com.huseyin.personalfinanceapi.category.exception.CategoryNotFoundException;
import com.huseyin.personalfinanceapi.common.exception.CurrencyMismatchException;
import com.huseyin.personalfinanceapi.common.exception.InvalidTransactionTypeException;
import com.huseyin.personalfinanceapi.common.exception.MoneyAdditionException;
import com.huseyin.personalfinanceapi.common.exception.MoneyCreationException;
import com.huseyin.personalfinanceapi.common.exception.MoneySubstractionException;
import com.huseyin.personalfinanceapi.schedule.exception.InvalidScheduleStateException;
import com.huseyin.personalfinanceapi.schedule.exception.ScheduledTransactionNotFoundException;
import com.huseyin.personalfinanceapi.security.exception.ReuseRevokedTokenException;
import com.huseyin.personalfinanceapi.transaction.entry.NotAValidDirection;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.exception.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@org.springframework.web.bind.annotation.RestControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(ReuseRevokedTokenException.class)
    public ResponseEntity<?> handleReuseRevokedToken(ReuseRevokedTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body(exception.getMessage()));
    }

    @ExceptionHandler({AccountNotFoundException.class, CategoryNotFoundException.class,
            TransactionNotFoundException.class, ScheduledTransactionNotFoundException.class})
    public ResponseEntity<?> notFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(ex.getMessage()));
    }

    @ExceptionHandler({AccountAccessDeniedException.class, CategoryAccessDeniedException.class})
    public ResponseEntity<?> accessDenied(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body(ex.getMessage()));
    }

    @ExceptionHandler({BusinessRuleViolationException.class,
            BuiltInCategoryModificationException.class,
            CategoryInUseException.class,
            InvalidAccountTypeException.class,
            InvalidTransactionTypeException.class,
            InvalidScheduleStateException.class,
            CurrencyMismatchException.class,
            MoneyAdditionException.class,
            MoneySubstractionException.class,
            MoneyCreationException.class,
            NotAValidDirection.class,
            IllegalArgumentException.class,
            IllegalStateException.class})
    public ResponseEntity<?> conflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(ex.getMessage()));
    }

    private Map<String, Object> body(String message) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "message", message != null ? message : "Beklenmeyen hata"
        );
    }
}
