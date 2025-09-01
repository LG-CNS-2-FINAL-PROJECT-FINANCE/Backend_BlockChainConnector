package com.ddiring.Backend_BlockchainConnector.domain.mapper;

import com.ddiring.Backend_BlockchainConnector.domain.dto.trade.DepositDto;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deposit;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;

public class DepositMapper {
    private static void validateSmartContract(Deployment contract) {
        if (contract == null) {
            throw new NullPointerException("Smart Contract is null");
        }

        if (contract.getIsActive() == false) {
            throw new IllegalArgumentException("Smart Contract is not activated");
        }
    }

    private static void validateDepositDto(DepositDto depositDto) {
        if (depositDto == null) {
            throw new NullPointerException("depositDto is null");
        }
    }

    public static Deposit toEntity(Deployment contract, DepositDto depositDto, Deposit.DepositType depositType) {
        validateSmartContract(contract);

        validateDepositDto(depositDto);

        return Deposit.builder()
                .deployment(contract)
                .sellId(depositDto.getSellId())
                .userId(depositDto.getSellerAddress())
                .tokenAmount(depositDto.getTokenAmount().longValue())
                .depositType(depositType)
                .build();
    }
}
