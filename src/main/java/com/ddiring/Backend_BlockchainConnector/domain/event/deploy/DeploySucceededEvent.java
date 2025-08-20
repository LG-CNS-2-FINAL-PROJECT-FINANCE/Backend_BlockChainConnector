package com.ddiring.Backend_BlockchainConnector.domain.event.deploy;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeploySucceededEvent {
    public static final String TOPIC = "DEPLOY";

    // --- Header ---
    private String eventId;
    private String eventType;
    private Instant timestamp;

    // --- Payload ---
    private DeploySucceededPayload payload;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeploySucceededPayload {
        private String projectId;
        private String status;
    }

    public static DeploySucceededEvent of(String projectId) {
        String uuid = java.util.UUID.randomUUID().toString();
        String eventType = TOPIC + ".SUCCEEDED";

        return DeploySucceededEvent.builder()
                .eventId(uuid)
                .eventType(eventType)
                .timestamp(Instant.now())
                .payload(DeploySucceededPayload.builder()
                        .projectId(projectId)
                        .status("SUCCEEDED")
                        .build()
                )
                .build();
    }
}
