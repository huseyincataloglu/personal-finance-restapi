package com.huseyin.personalfinanceapi.transaction.service;

import com.huseyin.personalfinanceapi.account.entity.Account;
import com.huseyin.personalfinanceapi.account.entity.AssetAccount;
import com.huseyin.personalfinanceapi.account.entity.BalanceAccount;
import com.huseyin.personalfinanceapi.account.exception.AccountAccessDeniedException;
import com.huseyin.personalfinanceapi.category.entity.Category;
import com.huseyin.personalfinanceapi.common.Money;
import com.huseyin.personalfinanceapi.common.TransactionType;
import com.huseyin.personalfinanceapi.common.accessgate.UserAccessGate;
import com.huseyin.personalfinanceapi.transaction.controller.dto.*;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.AssetEntry;
import com.huseyin.personalfinanceapi.transaction.entry.CashEntry;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.TransactionNotFoundException;
import com.huseyin.personalfinanceapi.transaction.processor.TransactionProcessorRegistry;
import com.huseyin.personalfinanceapi.transaction.processor.command.*;
import com.huseyin.personalfinanceapi.transaction.processor.processors.TransactionProcessor;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import com.huseyin.personalfinanceapi.transaction.resolver.AccountAccessResolver;
import com.huseyin.personalfinanceapi.transaction.resolver.CategoryResolver;
import com.huseyin.personalfinanceapi.transaction.validator.TransactionValidator;
import com.huseyin.personalfinanceapi.user.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Tüm işlem oluşturma ve bakiye/holding güncellemelerini <b>atomik</b> ve
 * <b>kural-doğrulamalı</b> şekilde yapan tek nokta. Hesap servisinin yaptığı
 * şey hesabı yaratmak veya okumaktır; bakiye değişimi sadece bu sınıftaki
 * metotlardan geçer.
 *
 * <p>Talimat gereği işlemler silinemez; ters kayıt için {@link #reverse(Long, Long)}
 * kullanılır.
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserAccessGate userAccessGate;
    private final AccountAccessResolver accountResolver;
    private final CategoryResolver categoryResolver;
    private final TransactionProcessorRegistry processorRegistry;

    public TransactionService(TransactionRepository transactionRepository, UserAccessGate userAccessGate,
                              AccountAccessResolver accountResolver, CategoryResolver categoryResolver,
                              TransactionProcessorRegistry processorRegistry) {
        this.transactionRepository = transactionRepository;
        this.userAccessGate = userAccessGate;
        this.accountResolver = accountResolver;
        this.categoryResolver = categoryResolver;
        this.processorRegistry = processorRegistry;
    }

    // ---------------------------------------------------------------------
    //  INITIAL BALANCE
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordInitialBalance(Long userId, CreateInitialBalanceRequest req) {

        User user = userAccessGate.requireFinancialAccess(userId);

        // Validates user ownership of the requested account, account acitivity
        Account account = accountResolver.resolveOwnedAccount(req.accountId(), userId);

        TransactionProcessor<TransactionCommand> initialBalanceProcessor =
                processorRegistry.get(TransactionType.INITIAL_BALANCE);

        return initialBalanceProcessor.process(
                new InitialBalanceCommand(
                        user,account,req.amount(),req.description(),Instant.now()
                )
        );

    }

    // ---------------------------------------------------------------------
    //  INCOME
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordIncome(Long userId, CreateIncomeRequest req) {

        User user = userAccessGate.requireFinancialAccess(userId);

        // Validates user activity,ownership of the requested account, account acitivity
        Account account = accountResolver.resolveOwnedAccount(req.accountId(), userId);

        //Resolve category object that suits for the transaction
        Category category = categoryResolver.resolveForTransaction(
                req.categoryId(), userId);

        TransactionProcessor<TransactionCommand> processor =
                processorRegistry.get(TransactionType.INCOME);

        return processor.process(
                new IncomeCommand(
                        user,
                        account,
                        req.amount(),
                        req.description(),
                        req.dateAndTime(),
                        category
                )
        );
    }

    // ---------------------------------------------------------------------
    //  EXPENSE
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordExpense(Long userId,CreateExpenseRequest req) {

        User user = userAccessGate.requireFinancialAccess(userId);
        // Validates user activity,ownership of the requested account, account acitivity
        Account account = accountResolver.resolveOwnedAccount(req.accountId(), userId);

        Category category = categoryResolver.resolveForTransaction(req.categoryId(), userId);

        TransactionProcessor<TransactionCommand> processor =
                processorRegistry.get(TransactionType.EXPENSE);

        return processor.process(
                new ExpenseCommand(
                        user,
                        (BalanceAccount) account,
                        req.amount(),
                        req.description(),
                        req.dateAndTime(),
                        category
                )
        );
    }

    // ---------------------------------------------------------------------
    //  TRANSFER
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordTransfer(Long userId, CreateTransferRequest req) {

        User user = userAccessGate.requireFinancialAccess(userId);

        Account source = accountResolver.resolveOwnedAccount(req.sourceAccountId(),userId);
        Account destination = accountResolver.resolveOwnedAccount(req.destinationAccountId(),userId);


        TransactionProcessor<TransactionCommand> processor =
                processorRegistry.get(TransactionType.TRANSFER);

        return processor.process(
                new TransferCommand(
                        user,
                        source,
                        destination,
                        req.amount(),
                        req.description(),
                        req.dateAndTime()
                )
        );

    }

    // ---------------------------------------------------------------------
    //  EXCHANGE
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordExchange(Long userId, CreateExchangeRequest req) {

        User user = userAccessGate.requireFinancialAccess(userId);

        Account source = accountResolver.resolveOwnedAccount(req.sourceAccountId(),userId);
        Account destination = accountResolver.resolveOwnedAccount(req.destinationAccountId(),userId);


        TransactionProcessor<TransactionCommand> processor =
                processorRegistry.get(TransactionType.EXCHANGE);

        return processor.process(
                new ExchangeCommand(user,  source,
                        destination,
                        req.sourceAmount(),
                        req.destinationAmount(),
                        req.appliedRate(),
                        req.description(),
                        req.dateAndTime())
        );


    }

    // ---------------------------------------------------------------------
    //  ASSET PURCHASE
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordAssetPurchase(Long userId,
                                           CreateAssetPurchaseRequest req) {
        User user = userAccessGate.requireFinancialAccess(userId);
        Account source = accountResolver.resolveOwnedAccount(req.sourceCashAccountId(),userId);
        Account destination = accountResolver.resolveOwnedAccount(req.assetAccountId(),userId);





    }

    // ---------------------------------------------------------------------
    //  ASSET SELL
    // ---------------------------------------------------------------------

    @Transactional
    public Transaction recordAssetSell(Long userId,
                                       CreateAssetSellRequest req) {
        User user = userAccessGate.requireFinancialAccess(userId);

        AssetAccount source = (AssetAccount) accountResolver.resolveOwnedAccount(req.assetAccountId(),userId);
        BalanceAccount destination = (BalanceAccount)accountResolver.resolveOwnedAccount(req.destinationCashAccountId(),userId);


        validator.validateOperationAllowed(destination,TransactionType.ASSET_SELL);
        validator.validateOperationAllowed(source, TransactionType.ASSET_SELL);
        validator.validateAssetSell(source, destination);

        Money unitPrice = new Money(req.unitPriceAmount(), req.unitPriceCurrencyCode());
        Money totalCashMoney = new Money(req.totalCashAmount(), req.unitPriceCurrencyCode());

        Transaction tx = transactionFactory.createTransaction(user, TransactionType.ASSET_SELL,
                req.description(), nullToNow(req.dateAndTime()),null,null);

        AssetEntry out = new AssetEntry(req.assetSymbol(), req.assetUnit(),
                req.quantity(), unitPrice, Entry.Direction.OUTWARD);

        out.setAccount(source);
        CashEntry in = new CashEntry(totalCashMoney, Entry.Direction.INWARD);
        in.setAccount(destination);

        tx.addEntry(out);
        tx.addEntry(in);

        applyAssetSell(source, req.assetSymbol(), req.assetUnit(), req.quantity());
        applyCashDelta(destination, in.getAmount(),in.getDirection());
        return transactionRepository.save(tx);
    }

    // ---------------------------------------------------------------------
    //  REVERSE
    // ---------------------------------------------------------------------

    /**
     * Daha önceki bir işlemi nötrler. Orijinal işlem silinmez; bunun yerine her
     * entry'sinin yönü ters çevrilmiş yeni bir REVERSE işlemi oluşturulur ve
     * hem orijinal hem reverse işlemi {@code active=false} olarak işaretlenir.
     */
    @Transactional
    public Transaction reverse(Long userId, Long originalId) {

        User user = userAccessGate.requireFinancialAccess(userId);

        Transaction original = transactionRepository.findById(originalId)
                .orElseThrow(() -> new TransactionNotFoundException(originalId));

        if (!original.getUser().getId().equals(userId)) {
            throw new AccountAccessDeniedException(
                    "Bu işleme erişim yetkiniz yok: id=" + originalId);
        }


        Transaction reverseTx = transactionFactory.createReverseTransaction(original);

        List<Entry> reversedEntries = reverseEntries(original.getEntryList(),reverseTx);

        for (Entry reversed : reversedEntries) {
            if (reversed instanceof CashEntry ce) {
                reverseTx.addEntry(ce);
                applyCashDelta((BalanceAccount) ce.getAccount(), ce.getAmount(), ce.getDirection());
            } else if (reversed instanceof AssetEntry ae) {
                reverseTx.addEntry(reversed);
                AssetAccount asset = (AssetAccount) ae.getAccount();
                if (reversed.getDirection() == Entry.Direction.INWARD) {
                    applyAssetPurchase(asset, ae.getAssetSymbol(), ae.getAssetUnit(),
                            ae.getQuantity(), ae.getUnitPrice());
                } else {
                    applyAssetSell(asset, ae.getAssetSymbol(), ae.getAssetUnit(), ae.getQuantity());
                }
            }
        }

        transactionRepository.save(original);
        return transactionRepository.save(reverseTx);
    }


    private List<Entry> reverseEntries(
            List<Entry> originals,Transaction reverseTx
    ){
        List<Entry> reversedList = new ArrayList<>();
        for (Entry entry:originals){
            Entry reversedEntry = entry.reverse();
            reversedEntry.setTransaction(reverseTx);
            reversedList.add(reversedEntry);
        }
        return reversedList;

    }




    private Instant nullToNow(Instant value) {
        return value != null ? value : Instant.now();
    }

}
