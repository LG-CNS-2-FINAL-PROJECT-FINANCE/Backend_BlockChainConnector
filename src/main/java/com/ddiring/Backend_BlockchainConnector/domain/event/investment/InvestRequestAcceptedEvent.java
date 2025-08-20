package com.ddiring.Backend_BlockchainConnector.domain.event.investment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class InvestRequestAcceptedEvent {
    public static final String TOPIC = "invest-request-accepted";

    // --- Header ---
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

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

    public static InvestRequestAcceptedEvent of(Long investmentId) {
        String uuid = java.util.UUID.randomUUID().toString();

        return InvestRequestAcceptedEvent.builder()
                .eventId(uuid)
                .eventType(TOPIC)
                .timestamp(LocalDateTime.now())
                .payload(InvestRequestAcceptedPayload.builder()
                        .investmentId(investmentId)
                        .status("ACCEPTED")
                        .build()
                )
                .build();
    }
}
