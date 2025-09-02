package com.ddiring.Backend_BlockchainConnector.domain.event.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TradeRequestRejectedEvent {
    public static final String TOPIC = "TRADE";

    private String eventId;
    private String eventType;
    private Instant timestamp;

    private TradeRequestRejectedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TradeRequestRejectedPayload {
        private String projectId;
        private Long tradeId;
        private String status;
        private String errorMessage;
    }

    public static TradeRequestRejectedEvent of(String projectId, Long tradeId, String errorMessage) {
        String uuid = java.util.UUID.randomUUID().toString();
        String eventType = TOPIC + ".REQUEST.REJECTED";

        return TradeRequestRejectedEvent.builder()
                .eventId(uuid)
                .eventType(eventType)
                .timestamp(Instant.now())
                .payload(TradeRequestRejectedPayload.builder()
                        .projectId(projectId)
                        .tradeId(tradeId)
                        .status("REJECTED")
                        .errorMessage(errorMessage)
                        .build()
                )
                .build();
    }
}
