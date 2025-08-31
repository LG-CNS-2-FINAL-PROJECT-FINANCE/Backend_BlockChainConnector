package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionResult {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    PENDING("PENDING");

    private final String transactionResult;

    TransactionResult(String result) {
        transactionResult = result;
    }
}
