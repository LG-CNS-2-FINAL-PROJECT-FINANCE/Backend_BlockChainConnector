package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DepositWithPermitDto {
    @NotBlank
    private String smartContractAddress;

    @NotNull
    private Long tradeId;

    @NotBlank
    private String sellerAddress;

    @NotBlank
    private String buyerAddress;

    @NotNull
    private BigInteger tokenAmount;

    @NotNull
    private BigInteger deadline;

    @NotNull
    private Integer v;

    @NotBlank
    private String r;

    @NotBlank
    private String s;
}
