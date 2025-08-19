package com.ddiring.Backend_BlockchainConnector.domain.event.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradeSucceededEvent {
    public static final String TOPIC = "trade-succeeded";

    private String eventId;
    private String eventType;
    private String timestamp;

    private TradeSucceededPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TradeSucceededPayload {
        private Long tradeId;
        private String status;
        private String buyerAddress;
        private String buyerTokenAmount;
        private String sellerAddress;
        private String sellerTokenAmount;
    }
}
