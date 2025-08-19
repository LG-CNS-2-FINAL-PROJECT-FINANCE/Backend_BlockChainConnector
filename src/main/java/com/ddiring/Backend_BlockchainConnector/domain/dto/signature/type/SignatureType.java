package com.ddiring.Backend_BlockchainConnector.domain.dto.signature.type;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignatureType {
    private String name;
    private String type;
}
