package com.ddiring.Backend_BlockchainConnector.domain.dto.trade;

import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class DepositWithPermitDto {
    @NotBlank
    private String projectId;

    @NotNull
    private Long sellId;

    @NotBlank
    private String sellerAddress;

    @NotNull
    private BigInteger tokenAmount;

    @NotNull
    private BigInteger deadline;

    @NotNull
    private Integer v;

    @NotBlank
    private String r;

    @NotBlank
    private String s;

    public static BlockchainLog toEntityForDepositSucceeded(SmartContract contract, String transactionHash) {
        if (contract == null) {
            throw new NullPointerException("contract is null");
        }

        if (contract.getIsActive() == false) {
            throw new IllegalArgumentException("contract is inactive");
        }

        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.DEPOSIT)
                .requestStatus(BlockchainRequestStatus.SUCCESS)
                .transactionHash(transactionHash)
                .build();
    }

    public static BlockchainLog toEntityForDepositFailed(SmartContract contract) {
        if (contract == null) {
            throw new NullPointerException("contract is null");
        }

        if (contract.getIsActive() == false) {
            throw new IllegalArgumentException("contract is inactive");
        }

        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.DEPOSIT)
                .requestStatus(BlockchainRequestStatus.FAILURE)
                .build();
    }
}
