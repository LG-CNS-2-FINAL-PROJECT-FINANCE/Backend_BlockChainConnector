package com.ddiring.Backend_BlockchainConnector.domain.event.investment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class InvestFailedEvent {
    public static final String TOPIC = "invest-failed";

    // --- Header ---
    private String eventId;
    private String eventType;
    private String timestamp;

    // --- Payload ---
    private InvestFailedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class InvestFailedPayload {
        private Long investmentId;
        private String status;
        private String investorAddress;
        private Long tokenAmount;
        private String errorMessage;
    }
}
