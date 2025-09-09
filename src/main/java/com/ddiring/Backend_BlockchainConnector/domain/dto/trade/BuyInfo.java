package com.ddiring.Backend_BlockchainConnector.domain.dto.trade;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BuyInfo {
    private Long buyId;
    private String buyerAddress;
}
