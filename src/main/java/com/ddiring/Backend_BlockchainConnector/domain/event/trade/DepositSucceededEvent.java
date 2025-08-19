package com.ddiring.Backend_BlockchainConnector.domain.event.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class DepositSucceededEvent {
    public static final String TOPIC = "deposit-succeeded";

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    private DepositSucceededPayload payload;

    public static class DepositSucceededPayload {
        private Long sellId;
        private String status;
        private String sellerAddress;
        private String sellerTokenAmount;
    }
}
