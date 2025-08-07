package com.ddiring.Backend_BlockchainConnector.domain.dto;

import lombok.Builder;
import lombok.Getter;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

@Getter
@Builder
public class SolidityFunctionWrapperDto {
    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider gasProvider;
}
