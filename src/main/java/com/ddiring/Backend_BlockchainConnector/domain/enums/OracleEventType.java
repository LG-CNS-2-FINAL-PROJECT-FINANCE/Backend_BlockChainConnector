package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum OracleEventType {
    INVESTMENT_SUCCESSFUL("investmentSuccessful"),
    INVESTMENT_FAILED("investmentFailed"),
    TRADE_SUCCESSFUL("tradeSuccessful"),
    TRADE_FAILED("tradeFailed");

    private final String eventName;

    OracleEventType(String eventName) {
        this.eventName = eventName;
    }

    public static List<OracleEventType> getAllEvent() {
        return List.of(OracleEventType.values());
    }
}
