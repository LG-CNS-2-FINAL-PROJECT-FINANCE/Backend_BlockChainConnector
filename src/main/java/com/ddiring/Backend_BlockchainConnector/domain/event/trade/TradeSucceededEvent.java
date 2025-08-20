package com.ddiring.Backend_BlockchainConnector.domain.event.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradeSucceededEvent {
    public static final String TOPIC = "trade-succeeded";

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

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

    public static TradeSucceededEvent of(Long tradeId, String buyerAddress, String buyerTokenAmount,
                                          String sellerAddress, String sellerTokenAmount) {
        String uuid = java.util.UUID.randomUUID().toString();

        return TradeSucceededEvent.builder()
                .eventId(uuid)
                .eventType(TOPIC)
                .timestamp(LocalDateTime.now())
                .payload(TradeSucceededPayload.builder()
                        .tradeId(tradeId)
                        .status("SUCCEEDED")
                        .buyerAddress(buyerAddress)
                        .buyerTokenAmount(buyerTokenAmount)
                        .sellerAddress(sellerAddress)
                        .sellerTokenAmount(sellerTokenAmount)
                        .build()
                )
                .build();
    }
}
