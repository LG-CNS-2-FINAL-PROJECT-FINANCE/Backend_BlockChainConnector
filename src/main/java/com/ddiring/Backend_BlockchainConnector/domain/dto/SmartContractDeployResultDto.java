package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmartContractDeployResultDto {
    @NotBlank(message = "조각 투자 상품 아이디를 입력하시오.")
    private String projectId;

    @NotBlank(message = "스마트 컨트랙트 주소를 입력하시오.")
    private String address;

    @NotBlank(message = "트랜잭션 해시를 입력하시오.")
    private String transactionHash;

    @NotBlank(message = "배포 파이프라인 응답 결과를 입력하시오.")
    private String status;
}
