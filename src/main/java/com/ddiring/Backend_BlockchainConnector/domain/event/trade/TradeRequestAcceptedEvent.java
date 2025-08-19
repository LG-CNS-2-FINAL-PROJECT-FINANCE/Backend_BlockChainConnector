package com.ddiring.Backend_BlockchainConnector.domain.event.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradeRequestAcceptedEvent {
    public static final String TOPIC = "trade-request-accepted";

    private String eventId;
    private String eventType;
    private String timestamp;

    private TradeRequestAcceptedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TradeRequestAcceptedPayload {
        private Long tradeId;
        private String status;
    }
}
