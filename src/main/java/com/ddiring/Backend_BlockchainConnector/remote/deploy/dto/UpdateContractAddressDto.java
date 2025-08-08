package com.ddiring.Backend_BlockchainConnector.remote.deploy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateContractAddressDto {
    @NotBlank(message = "조각 투자 상품 아이디를 입력하시오.")
    private String projectId;

    @NotBlank(message = "배포된 스마트 컨트랙트 주소를 입력하시오.")
    private String smartContractAddress;
}
