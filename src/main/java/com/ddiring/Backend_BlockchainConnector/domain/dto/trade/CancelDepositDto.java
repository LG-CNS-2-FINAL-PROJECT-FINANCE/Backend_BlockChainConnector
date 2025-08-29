package com.ddiring.Backend_BlockchainConnector.domain.dto.trade;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigInteger;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelDepositDto {
    @NotBlank
    private String projectId;

    @NotNull
    private Long sellId;

    @NotBlank
    private String sellerAddress;

    @NotNull
    private BigInteger tokenAmount;

    @NotBlank
    private String hashedMessage;

    @NotNull
    private Integer v;

    @NotBlank
    private String r;

    @NotBlank
    private String s;
}
