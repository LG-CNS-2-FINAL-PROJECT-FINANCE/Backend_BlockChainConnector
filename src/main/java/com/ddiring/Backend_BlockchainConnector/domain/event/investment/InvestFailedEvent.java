package com.ddiring.Backend_BlockchainConnector.domain.event.investment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class InvestFailedEvent {
    public static final String TOPIC = "invest-failed";

    // --- Header ---
    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

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

    public static InvestFailedEvent of(Long investmentId, String investorAddress, Long tokenAmount, String errorMessage) {
        String uuid = java.util.UUID.randomUUID().toString();

        return InvestFailedEvent.builder()
                .eventId(uuid)
                .eventType(TOPIC)
                .timestamp(LocalDateTime.now())
                .payload(InvestFailedPayload.builder()
                        .investmentId(investmentId)
                        .status("FAILED")
                        .investorAddress(investorAddress)
                        .tokenAmount(tokenAmount)
                        .errorMessage(errorMessage)
                        .build()
                )
                .build();
    }
}
