package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmartContractDeployDto {
    @NotBlank(message = "조각 투자 상품 아이디를 입력하시오.")
    private String projectId;

    @NotBlank(message="조각 투자 상품명을 입력하시오.")
    private String tokenName;

    @NotBlank(message = "조각 투자 심볼을 입력하시오.")
    private String tokenSymbol;

    @NotNull(message = "조각 투자 상품의 목표 금액을 입력하시오.")
    @Positive(message = "목표 금액은 0보다 커야 합니다.")
    private Long totalGoalAmount;

    @NotNull(message = "조각 투자 상품의 최소 투자 금액을 입력하시오.")
    @Positive(message = "최소 금액은 0보다 커야 합니다.")
    private Long minAmount;


}
