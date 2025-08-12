package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeDto {
    @NotNull(message = "거래 번호는 필수입니다.")
    @Positive(message = "거래 번호는 양수여야 합니다.")
    private Long tradeId;

    @NotBlank(message = "스마트 계약 주소는 필수입니다.")
    private String smartContractAddress;

    @NotBlank(message = "구매자 주소는 필수입니다.")
    private String buyerAddress;

    @NotBlank(message = "판매자 주소는 필수입니다.")
    private String sellerAddress;

    @NotNull(message = "토큰 금액은 필수입니다.")
    @Positive(message = "토큰 금액은 양수여야 합니다.")
    private Long tokenAmount;
}
