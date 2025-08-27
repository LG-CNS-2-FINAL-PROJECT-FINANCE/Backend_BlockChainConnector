package com.ddiring.Backend_BlockchainConnector.domain.dto;

import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InvestmentDto {
    @NotBlank(message = "프로젝트 아이디를 입력하시오.")
    private String projectId;

    @NotNull
    private List<InvestInfo> investInfoList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class InvestInfo {
        @NotNull(message = "투자 아이디를 입력하시오.")
        @Positive(message = "투자 아이디는 양수로 구성되어야 합니다.")
        private Long investmentId;

        @NotBlank(message = "투자자 지갑 주소를 입력하시오.")
        private String investorAddress;

        @NotNull(message = "투자할 토큰 개수를 입력하시오.")
        @Positive(message = "투자할 토큰 개수는 1개 이상이어야 합니다.")
        private Long tokenAmount;
    }

    public List<FractionalInvestmentToken.investment> toSmartContractStruct() {
        if (investInfoList == null || investInfoList.isEmpty()) {
            return null;
        }

        return this.investInfoList.stream().map(investInfo -> {
            return new FractionalInvestmentToken.investment(
                investInfo.investmentId.toString(),
                investInfo.investorAddress,
                BigInteger.valueOf(investInfo.tokenAmount),
                false
            );
        }).toList();
    }
}
