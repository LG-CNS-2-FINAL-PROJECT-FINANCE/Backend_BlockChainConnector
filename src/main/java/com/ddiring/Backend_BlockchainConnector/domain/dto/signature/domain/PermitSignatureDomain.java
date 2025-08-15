package com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.math.BigInteger;

@Getter
@Builder
public class PermitSignatureDomain {
    @NonNull
    private String name;

    @NonNull
    private String version;

    @NonNull
    private BigInteger chainId;

    @NonNull
    private String verifyingContract;
}
