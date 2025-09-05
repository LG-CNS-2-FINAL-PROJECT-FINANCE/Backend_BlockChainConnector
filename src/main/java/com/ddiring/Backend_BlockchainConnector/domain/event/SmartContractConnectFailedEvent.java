package com.ddiring.Backend_BlockchainConnector.domain.event;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class SmartContractConnectFailedEvent {
    public static final String TOPIC = "CONTRACT_CONNECT";

    private String eventId;
    private String eventType;          // CONTRACT_DEACTIVATED, CONTRACT_ADDED, CONTRACT_REMOVED 등
    private Instant timestamp;
    private SmartContractConnectFailedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class SmartContractConnectFailedPayload {
        private String projectId;
        private String contractAddress;
        private Integer attempts;
    }

    public static SmartContractConnectFailedEvent of(String projectId, String contractAddress, Integer attempts) {
        String eventId = UUID.randomUUID().toString();
        String eventType = TOPIC + ".FAILED";

        return SmartContractConnectFailedEvent.builder()
                .eventId(eventId)
                .eventType(eventType)
                .timestamp(Instant.now())
                .payload(SmartContractConnectFailedPayload.builder()
                        .projectId(projectId)
                        .contractAddress(contractAddress)
                        .attempts(attempts)
                        .build())
                .build();
    }
}
