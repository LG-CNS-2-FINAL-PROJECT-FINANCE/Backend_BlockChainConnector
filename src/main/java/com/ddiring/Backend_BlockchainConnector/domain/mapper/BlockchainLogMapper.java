package com.ddiring.Backend_BlockchainConnector.domain.mapper;

import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;

public class BlockchainLogMapper {
    private static void validateContract(Deployment contract) {
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

    public static BlockchainLog toEntityForInvestment(Deployment contract, String requestTransactionHash, Long orderId) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .deployment(contract)
                .requestType(BlockchainRequestType.INVESTMENT)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .requestTransactionHash(requestTransactionHash)
                .orderId(orderId)
                .build();
    }

    public static BlockchainLog toEntityForDeposit(Deployment contract, Long orderId) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .deployment(contract)
                .requestType(BlockchainRequestType.DEPOSIT)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .orderId(orderId)
                .build();
    }

    public static BlockchainLog toEntityForCancelDeposit(Deployment contract, Long orderId) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .deployment(contract)
                .requestType(BlockchainRequestType.CANCEL_DEPOSIT)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .orderId(orderId)
                .build();
    }

    public static BlockchainLog toEntityForTrade(Deployment contract, String requestTransactionHash, Long orderId) {
        validateContract(contract);
        return BlockchainLog.builder()
                .projectId(contract.getProjectId())
                .deployment(contract)
                .requestType(BlockchainRequestType.TRADE)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .requestTransactionHash(requestTransactionHash)
                .orderId(orderId)
                .build();
    }
}
