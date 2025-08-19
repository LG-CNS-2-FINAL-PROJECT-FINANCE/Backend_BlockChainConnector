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
public class DepositFailedEvent {
    public static final String TOPIC = "deposit-failed";

    private String eventId;
    private String eventType;
    private LocalDateTime timestamp;

    private DepositFailedPayload payload;

    public static class DepositFailedPayload {
        private Long sellId;
        private String status;
        private String sellerAddress;
        private String sellerTokenAmount;
        private String errorMessage;
    }
}
