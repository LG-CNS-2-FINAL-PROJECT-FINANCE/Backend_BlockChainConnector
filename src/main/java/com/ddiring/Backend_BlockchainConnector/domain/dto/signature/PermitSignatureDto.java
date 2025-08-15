package com.ddiring.Backend_BlockchainConnector.domain.dto.signature;

import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain.PermitSignatureDomain;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.message.PermitSignatureMessage;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.type.PermitSignatureTypes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class PermitSignatureDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String smartContractAddress;

        @NotBlank
        private String userAddress;

        @NotNull
        @Positive
        private Long tokenAmount;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Response {
        @NotNull
        private PermitSignatureDomain domain;

        @NotNull
        private PermitSignatureTypes types;

        @NotNull
        private PermitSignatureMessage message;
    }
}
