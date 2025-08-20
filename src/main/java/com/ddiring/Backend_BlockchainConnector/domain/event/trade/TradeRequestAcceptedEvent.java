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
public class TradeRequestAcceptedEvent {
    public static final String TOPIC = "trade-request-accepted";

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    private TradeRequestAcceptedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TradeRequestAcceptedPayload {
        private Long tradeId;
        private String status;
    }

    public static TradeRequestAcceptedEvent of(Long tradeId) {
        String uuid = java.util.UUID.randomUUID().toString();

        return TradeRequestAcceptedEvent.builder()
                .eventId(uuid)
                .eventType(TOPIC)
                .timestamp(LocalDateTime.now())
                .payload(TradeRequestAcceptedPayload.builder()
                        .tradeId(tradeId)
                        .status("ACCEPTED")
                        .build()
                )
                .build();
    }
}
