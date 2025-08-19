package com.ddiring.Backend_BlockchainConnector.domain.event.investment;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class InvestRequestAcceptedEvent {
    public static final String TOPIC = "invest-request-accepted";

    // --- Header ---
    private String eventId;
    private String eventType;
    private String timestamp;

    // --- Payload ---
    private InvestRequestAcceptedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class InvestRequestAcceptedPayload {
        private Long investmentId;
        private String status;
    }
}
