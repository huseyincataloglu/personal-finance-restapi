package com.huseyin.personalfinanceapi.transaction.service;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.exception.AccountAccessDeniedException;
import com.huseyin.personalfinanceapi.account.exception.AccountNotFoundException;
import com.huseyin.personalfinanceapi.account.repository.AccountRepository;
import com.huseyin.personalfinanceapi.transaction.controller.dto.TransactionFilter;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.exception.TransactionNotFoundException;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Sadece okuma — bakiye değişimi yapmaz. {@link TransactionService} ile
 * tek-yönlü ilişki: TransactionService yazar, bu sınıf okur.
 *
 * <p>Specification + Pageable kombinasyonu ile gelişmiş filtreleme + sayfalama
 * destekler. Filtre alanları null/boş ise o ölçüt no-op olur.
 */
@Service
@Transactional(readOnly = true)
public class TransactionQueryService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionQueryService(TransactionRepository transactionRepository,
                                   AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /** Filtre + sayfalama ile kullanıcının tüm işlemlerini sorgular. */
    public Page<Transaction> search(Long userId, TransactionFilter filter, Pageable pageable) {
        Specification<Transaction> spec = TransactionSpecifications.fromFilter(userId, filter);
        return transactionRepository.findAll(spec, pageable);
    }

    /**
     * Tek bir hesabın işlemlerini filtre + sayfalama ile sorgular. Filtrede
     * accountIds set edilse de path'teki accountId zorla AND ile ek olarak
     * uygulanır.
     */
    public Page<Transaction> searchForAccount(Long userId, Long accountId,
                                              TransactionFilter filter, Pageable pageable) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException(
                    "Bu hesaba erişim yetkiniz yok: id=" + accountId);
        }
        Specification<Transaction> spec = TransactionSpecifications.fromFilter(userId, filter)
                .and(TransactionSpecifications.hasEntryInAccount(accountId));
        return transactionRepository.findAll(spec, pageable);
    }

    public List<Transaction> listForAccount(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
        if (!account.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException(
                    "Bu hesaba erişim yetkiniz yok: id=" + accountId);
        }
        return transactionRepository.findAllByAccountIdAndUserId(accountId, userId);
    }

    public Transaction getForUser(Long transactionId, Long userId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));
        if (!tx.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException(
                    "Bu işleme erişim yetkiniz yok: id=" + transactionId);
        }
        return tx;
    }
}
