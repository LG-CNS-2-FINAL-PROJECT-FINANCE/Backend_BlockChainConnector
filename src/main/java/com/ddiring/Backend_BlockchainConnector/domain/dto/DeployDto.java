package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

public class DeployDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
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


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        @NotBlank(message = "조각 투자 상품 아이디를 입력하시오.")
        private String projectId;

        @NotBlank(message = "스마트 컨트랙트 주소를 입력하시오.")
        private String address;

        @NotBlank(message = "트랜잭션 해시를 입력하시오.")
        private String transactionHash;

        @NotBlank(message = "배포 파이프라인 응답 결과를 입력하시오.")
        private String status;
    }
}
