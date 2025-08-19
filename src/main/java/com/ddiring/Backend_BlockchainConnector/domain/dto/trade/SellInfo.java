package com.ddiring.Backend_BlockchainConnector.domain.dto.trade;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SellInfo {
    private Long sellId;
    private String sellerAddress;
    private Long tokenAmount;
}