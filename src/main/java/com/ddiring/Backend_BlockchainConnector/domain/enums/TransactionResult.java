package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionResult {
    SUCCESS(0L),
    FAILURE(1L),
    PENDING(2L);

    private final Long transactionResult;

    TransactionResult(Long l) {
        transactionResult = l;
    }
}
