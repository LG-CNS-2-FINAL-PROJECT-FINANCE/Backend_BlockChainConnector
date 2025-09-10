package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

public class LogsDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Request {
        @NotBlank
        String projectId;
        @NotBlank
        String userAddress;
        @Positive
        Long page;
        @Positive
        Long offset;
        @NotBlank
        String sort;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
            @Positive
            Long blockNumber;
            @NotEmpty
            LocalDateTime timeStamp;
            @NotBlank
            String transactionHash;
            @NotBlank
            String fromAddress;
            @NotBlank
            String contractAddress;
            @NotBlank
            String toAddress;
            @Positive
            Long value;
    }
}
