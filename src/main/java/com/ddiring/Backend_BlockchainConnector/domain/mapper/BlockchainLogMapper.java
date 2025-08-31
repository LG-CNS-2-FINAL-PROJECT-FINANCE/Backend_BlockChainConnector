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

    public static BlockchainLog toEntityForDepositSucceeded(SmartContract contract, String requestTransactionHash) {
        validateContract(contract);
        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.DEPOSIT)
                .requestStatus(BlockchainRequestStatus.SUCCESS)
                .requestTransactionHash(requestTransactionHash)
                .build();
    }

    public static BlockchainLog toEntityForDepositFailed(SmartContract contract) {
        validateContract(contract);
        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.DEPOSIT)
                .requestStatus(BlockchainRequestStatus.FAILURE)
                .build();
    }

    public static BlockchainLog toEntityForCancelDepositSucceeded(SmartContract contract, String requestTransactionHash) {
        validateContract(contract);
        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.CANCEL_DEPOSIT)
                .requestStatus(BlockchainRequestStatus.SUCCESS)
                .requestTransactionHash(requestTransactionHash)
                .build();
    }

    public static BlockchainLog toEntityForCancelDepositFailed(SmartContract contract) {
        validateContract(contract);
        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.CANCEL_DEPOSIT)
                .requestStatus(BlockchainRequestStatus.FAILURE)
                .build();
    }

    public static BlockchainLog toEntityForTrade(SmartContract contract, String requestTransactionHash) {
        validateContract(contract);
        return BlockchainLog.builder()
                .smartContractId(contract)
                .requestType(BlockchainRequestType.TRADE)
                .requestStatus(BlockchainRequestStatus.PENDING)
                .requestTransactionHash(requestTransactionHash)
                .build();
    }
}
