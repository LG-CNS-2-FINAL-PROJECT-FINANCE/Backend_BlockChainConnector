package com.ddiring.Backend_BlockchainConnector.domain.dto.signature.type;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class PermitSignatureTypes {

    @Builder.Default
    private Map<String, List<SignatureType>> types = Map.of(
        "Permit", List.of(
                new SignatureType("owner", "address"),
                new SignatureType("spender", "address"),
                new SignatureType("value", "uint256"),
                new SignatureType("nonce", "uint256"),
                new SignatureType("deadline", "uint256")
        )
    );
}

