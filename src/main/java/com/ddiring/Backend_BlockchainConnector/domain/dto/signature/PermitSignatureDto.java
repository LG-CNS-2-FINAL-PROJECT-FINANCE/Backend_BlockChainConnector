package com.ddiring.Backend_BlockchainConnector.domain.dto.signature;

import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain.PermitSignatureDomain;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.message.PermitSignatureMessage;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.type.SignatureType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
public class PermitSignatureDto {
    @Getter
    @Builder
    @NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String projectId;

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
        private PermitSignatureMessage message;

        @Builder.Default
        private Map<String, List<SignatureType>> types = Map.of(
                "Permit", List.of(
                    SignatureType.builder().name("owner").type("address").build(),
                    SignatureType.builder().name("spender").type("address").build(),
                    SignatureType.builder().name("value").type("uint256").build(),
                    SignatureType.builder().name("nonce").type("uint256").build(),
                    SignatureType.builder().name("deadline").type("uint256").build()
                )
        );
    }
}
