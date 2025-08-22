package com.ddiring.Backend_BlockchainConnector.domain.enums;

import lombok.Getter;

import java.util.List;

@Getter
public enum EventType {
    INVESTMENT_SUCCESSFUL("investmentSuccessful"),
    INVESTMENT_FAILED("investmentFailed"),
    TRADE_SUCCESSFUL("tradeSuccessful"),
    TRADE_FAILED("tradeFailed");

    private final String eventName;

    EventType(String eventName) {
        this.eventName = eventName;
    }

    public static List<EventType> getAllEvent() {
        return List.of(EventType.values());
    }
}
