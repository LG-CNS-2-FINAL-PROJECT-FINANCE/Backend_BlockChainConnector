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
public class TradeFailedEvent {
    public static final String TOPIC = "trade-failed";

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    private TradeFailedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TradeFailedPayload {
        private Long tradeId;
        private String status;
        private String buyerAddress;
        private String buyerTokenAmount;
        private String sellerAddress;
        private String sellerTokenAmount;
        private String errorMessage;
    }

    public static TradeFailedEvent of(Long tradeId, String buyerAddress, String buyerTokenAmount,
                                       String sellerAddress, String sellerTokenAmount, String errorMessage) {
        String uuid = java.util.UUID.randomUUID().toString();

        return TradeFailedEvent.builder()
                .eventId(uuid)
                .eventType(TOPIC)
                .timestamp(LocalDateTime.now())
                .payload(TradeFailedPayload.builder()
                        .tradeId(tradeId)
                        .status("FAILED")
                        .buyerAddress(buyerAddress)
                        .buyerTokenAmount(buyerTokenAmount)
                        .sellerAddress(sellerAddress)
                        .sellerTokenAmount(sellerTokenAmount)
                        .errorMessage(errorMessage)
                        .build()
                )
                .build();
    }
}
