package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

@Getter
public enum BlockchainRequestStatus {
    SUCCESS("SUCCESS"),
    FAILURE("FAILURE"),
    PENDING("PENDING");

    private final String transactionResult;

    BlockchainRequestStatus(String result) {
        transactionResult = result;
    }
}
