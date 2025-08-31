package com.ddiring.Backend_BlockchainConnector.domain.mapper;

import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;

public class BlockchainLogMapper {
    private static void validateContract(SmartContract contract) {
        if (contract == null) {
            throw new NullPointerException("contract is null");
        }
        if (!contract.getIsActive()) {
            throw new IllegalArgumentException("contract is inactive");
        }
    }

    public static BlockchainLog toEntityForDeploy(String projectId) {
        return BlockchainLog.builder()
                .projectId(projectId)
                .requestType(BlockchainRequestType.SMART_CONTRACT_DEPLOY)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .build();
    }

    public static BlockchainLog toEntityForInvestment(SmartContract contract, String requestTransactionHash, Long orderId) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .smartContract(contract)
                .requestType(BlockchainRequestType.INVESTMENT)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .requestTransactionHash(requestTransactionHash)
                .orderId(orderId)
                .build();
    }

    public static BlockchainLog toEntityForDeposit(SmartContract contract) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .smartContract(contract)
                .requestType(BlockchainRequestType.DEPOSIT)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .build();
    }

    public static BlockchainLog toEntityForCancelDeposit(SmartContract contract) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .smartContract(contract)
                .requestType(BlockchainRequestType.CANCEL_DEPOSIT)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .build();
    }

    public static BlockchainLog toEntityForTrade(SmartContract contract, String requestTransactionHash, Long orderId) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .smartContract(contract)
                .requestType(BlockchainRequestType.TRADE)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .requestTransactionHash(requestTransactionHash)
                .orderId(orderId)
                .build();
    }
}
