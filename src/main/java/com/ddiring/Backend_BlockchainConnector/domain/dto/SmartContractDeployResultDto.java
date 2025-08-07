package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmartContractDeployResultDto {
    @NotBlank(message = "스마트 컨트랙트 주소를 입력하시오.")
    private String address;

    @NotBlank(message="조각 투자 상품명을 입력하시오.")
    private String name;

    @NotBlank(message = "조각 투자 심볼을 입력하시오.")
    private String symbol;

}
