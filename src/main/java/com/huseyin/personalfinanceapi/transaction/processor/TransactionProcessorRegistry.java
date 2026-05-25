package com.huseyin.personalfinanceapi.transaction.processor;

import com.huseyin.personalfinanceapi.common.TransactionType;


import com.huseyin.personalfinanceapi.transaction.processor.command.TransactionCommand;
import com.huseyin.personalfinanceapi.transaction.processor.processors.TransactionProcessor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TransactionProcessorRegistry {

    private final Map<TransactionType, TransactionProcessor<?>> processorMap;

    public TransactionProcessorRegistry(
            List<TransactionProcessor<?>> processorList
    ){
        processorMap = processorList.stream()
                .collect(Collectors.toMap(
                        TransactionProcessor::getSupportedType,
                        Function.identity()
                ));

    }

    @SuppressWarnings("unchecked")
    public <T extends TransactionCommand>TransactionProcessor<T> get(TransactionType type){

        return (TransactionProcessor<T>) processorMap.get(type);
    }


}
