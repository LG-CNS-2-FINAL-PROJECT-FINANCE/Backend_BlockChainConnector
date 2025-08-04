package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SmartContractDeployDto {
    @NotBlank(message="조각 투자 상품명을 입력하시오.")
    private String tokenName;

    @NotBlank(message = "조각 투자 심볼을 입력하시오.")
    private String tokenSymbol;

    @NotBlank(message = "조각 투자 상품의 목표 금액을 입력하시오.")
    private Long totalGoalAmount;

    @NotBlank(message = "조각 투자 상품의 최소 투자 금액을 입력하시오.")
    private Long minAmount;
}
