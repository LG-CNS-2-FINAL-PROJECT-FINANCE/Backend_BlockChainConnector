package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

public class EtherscanEventLogDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @Positive
        Long chainId;
        @NotBlank
        String module;
        @NotBlank
        String action;
        @NotBlank
        String contractaddress;
        @NotBlank
        String address;
        @Positive
        Long page;
        @Positive
        Long offset;
        @Positive
        Long startblock;
        @Positive
        Long endblock;
        @NotBlank
        String sort;
        @NotBlank
        String apiKey;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        @NotBlank
        private String status;
        @NotBlank
        private String message;
        @NotEmpty
        private List<TokenTransaction> result;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class TokenTransaction {
            private Long blockNumber;
            private Long timeStamp;
            private String hash;
            private String nonce;
            private String blockHash;
            private String from;
            private String contractAddress;
            private String to;

            private BigInteger value;
            private BigInteger gas;
            private BigInteger gasPrice;
            private BigInteger gasUsed;
            private BigInteger cumulativeGasUsed;

            private String tokenName;
            private String tokenSymbol;
            private String tokenDecimal;
            private String transactionIndex;
            private String input;
            private Long confirmations;
        }
    }

    public static Request of (LogsDto.Request logsDto, Long chainId, String contractAddress, String apiKey) {
        return Request.builder()
                .chainId(chainId)
                .module("account")
                .action("tokentx")
                .contractaddress(contractAddress)
                .address(logsDto.getUserAddress())
                .startblock(0L)
                .endblock(Long.MAX_VALUE)
                .page(logsDto.getPage())
                .offset(logsDto.getOffset())
                .sort(logsDto.getSort())
                .apiKey(apiKey)
                .build();
    }
}
