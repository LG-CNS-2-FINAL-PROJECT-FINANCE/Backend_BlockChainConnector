package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BalanceDto {
    @Getter
    @Builder
    public static class Request {
        @NotBlank(message = "스마트 컨트랙트 주소를 입력하세요.")
        private String smartContractAddress;
        @NotBlank(message = "조회할 사용자 지갑 주소를 입력하세요.")
        private String userAddress;
    }

    @Getter
    @Builder
    public static class Response {
        @Positive(message = "토큰 개수는 0개 이상이어야 합니다.")
        private Long tokenAmount;
    }
}
