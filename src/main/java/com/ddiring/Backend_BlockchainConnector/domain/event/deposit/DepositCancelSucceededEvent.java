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
public class DepositCancelSucceededEvent {
    public static final String TOPIC = "DEPOSIT";

    private String eventId;
    private String eventType;
    private Instant timestamp;

    private DepositCancelSucceededPayload payload;

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class DepositCancelSucceededPayload {
        private Long sellId;
        private String status;
        private String sellerAddress;
        private Long sellerTokenAmount;
    }

    public static DepositCancelSucceededEvent of(Long sellId, String sellerAddress, Long sellerTokenAmount) {
        String uuid = java.util.UUID.randomUUID().toString();
        String eventType = TOPIC + ".CANCEL.SUCCEEDED";

        return DepositCancelSucceededEvent.builder()
                .eventId(uuid)
                .eventType(eventType)
                .timestamp(Instant.now())
                .payload(DepositCancelSucceededPayload.builder()
                        .sellId(sellId)
                        .status("SUCCEEDED")
                        .sellerAddress(sellerAddress)
                        .sellerTokenAmount(sellerTokenAmount)
                        .build()
                )
                .build();
    }
}
