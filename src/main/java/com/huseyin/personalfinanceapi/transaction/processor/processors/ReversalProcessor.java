package com.huseyin.personalfinanceapi.transaction.processor.processors;

import com.huseyin.personalfinanceapi.transaction.engine.TransactionEngine;
import com.huseyin.personalfinanceapi.transaction.entity.Transaction;
import com.huseyin.personalfinanceapi.transaction.entry.Entry;
import com.huseyin.personalfinanceapi.transaction.exception.BusinessRuleViolationException;
import com.huseyin.personalfinanceapi.transaction.processor.command.ReverseCommand;
import com.huseyin.personalfinanceapi.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReversalProcessor {

    private final TransactionRepository repository;
    private final TransactionEngine transactionEngine;

    public Transaction process(
            ReverseCommand command
    ){
        Transaction originalTran = command.originalTransaction();
        List<Entry> originalEntries = originalTran.getEntryList();


        if (originalTran.isReversed()|| originalTran.getReferenceTransactionId() !=null) {
            throw new BusinessRuleViolationException(
                    "This transaction is already reversed or reversal itself.");
        }

        Transaction reversalTransaction = originalTran.reverse();
        List<Entry> reversedEntries = reversedEntries(originalEntries);

        reversedEntries.forEach(reversalTransaction::addEntry);

        transactionEngine.




    }

    private List<Entry> reversedEntries(
            List<Entry> originals
    ){
        List<Entry> reversedList = new ArrayList<>();
        for (Entry entry:originals){
            Entry reversedEntry = entry.reverse();
            reversedList.add(reversedEntry);
        }
        return reversedList;

    }




}
