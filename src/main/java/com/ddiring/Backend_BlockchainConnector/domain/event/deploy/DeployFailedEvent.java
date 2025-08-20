package com.ddiring.Backend_BlockchainConnector.domain.event.deploy;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeployFailedEvent {
    public static final String TOPIC = "DEPLOY";

    // --- Header ---
    private String eventId;
    private String eventType;
    private Instant timestamp;

    // --- Payload ---
    private DeployFailedPayload payload;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeployFailedPayload {
        private String projectId;
        private String status;
        private String errorMessage;
    }

    public static DeployFailedEvent of(String projectId, String errorMessage) {
        String uuid = java.util.UUID.randomUUID().toString();
        String eventType = TOPIC + ".FAILED";

        return DeployFailedEvent.builder()
                .eventId(uuid)
                .eventType(eventType)
                .timestamp(Instant.now())
                .payload(DeployFailedPayload.builder()
                        .projectId(projectId)
                        .status("FAILED")
                        .errorMessage(errorMessage)
                        .build()
                )
                .build();
    }
}
