package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

@Getter
public enum OracleEventErrorType {
    REPEAT_FAILED("REPEAT_FAILED"),
    CHAINLINK_FAILED("CHAINLINK_FAILED"),
    SMART_CONTRACT_FAILED("SMART_CONTRACT_FAILED"),
    EXTERNAL_API_FAILED("EXTERNAL_API_FAILED");

    private final String errorType;

    OracleEventErrorType(String errorType) {
        this.errorType = errorType;
    }

    public static OracleEventErrorType fromValue(Long value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        if (value < 0L || values().length <= value) {
            throw new IllegalArgumentException("invalid value for EventErrorType");
        }

        return OracleEventErrorType.values()[value.intValue()];
    }
}
