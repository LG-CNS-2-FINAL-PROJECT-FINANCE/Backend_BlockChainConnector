package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

@Getter
public enum BlockchainRequestType {
    SMART_CONTRACT_DEPLOY("SMART_CONTRACT_DEPLOY"),
    INVESTMENT("INVESTMENT"),
    DEPOSIT("DEPOSIT"),
    CANCEL_DEPOSIT("CANCEL_DEPOSIT"),
    TRADE("TRADE");

    private final String requestType;

    BlockchainRequestType(String requestType) {
        this.requestType = requestType;
    }

}
