package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

@Getter
public enum EventErrorType {
    REPEAT_FAILED(0L),
    CHAINLINK_FAILED(1L),
    SMART_CONTRACT_FAILED(2L),
    EXTERNAL_API_FAILED(3L);

    private final Long errorType;

    EventErrorType(Long errorType) {
        this.errorType = errorType;
    }

}
