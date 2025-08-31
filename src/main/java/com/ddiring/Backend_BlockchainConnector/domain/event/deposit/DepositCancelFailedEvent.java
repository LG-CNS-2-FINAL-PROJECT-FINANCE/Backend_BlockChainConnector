package com.ddiring.Backend_BlockchainConnector.domain.event.deposit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class DepositCancelFailedEvent {
    public static final String TOPIC = "DEPOSIT";

    private String eventId;
    private String eventType;
    private Instant timestamp;

    private DepositCancelFailedPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class DepositCancelFailedPayload {
        private Long sellId;
        private String status;
        private String sellerAddress;
        private Long sellerTokenAmount;
        private String errorMessage;
    }

    public static DepositCancelFailedEvent of(Long sellId, String sellerAddress, Long sellerTokenAmount, String errorMessage) {
        String uuid = java.util.UUID.randomUUID().toString();
        String eventType = TOPIC + ".CANCEL.FAILED";

        return DepositCancelFailedEvent.builder()
                .eventId(uuid)
                .eventType(eventType)
                .timestamp(Instant.now())
                .payload(DepositCancelFailedPayload.builder()
                        .sellId(sellId)
                        .status("FAILED")
                        .sellerAddress(sellerAddress)
                        .sellerTokenAmount(sellerTokenAmount)
                        .errorMessage(errorMessage)
                        .build()
                )
                .build();
    }
}
