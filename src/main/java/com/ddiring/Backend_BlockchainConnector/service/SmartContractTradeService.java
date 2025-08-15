package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.dto.TradeDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.PermitSignatureDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain.PermitSignatureDomain;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.message.PermitSignatureMessage;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.type.PermitSignatureTypes;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractTradeService {
    private final ContractWrapper contractWrapper;

    public PermitSignatureDto.Response getSignature(@Valid PermitSignatureDto.Request permitSignatureDto) {
        try {
        FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                permitSignatureDto.getSmartContractAddress(),
                contractWrapper.getWeb3j(),
                contractWrapper.getCredentials(),
                contractWrapper.getGasProvider()
        );

        // Domain
        String smartContractName = smartContract.name().send();
        String smartContractVersion = "1";
        BigInteger chainId = contractWrapper.getWeb3j().ethChainId().send().getChainId();

        // Message
        BigInteger tokenAmountWei = BigInteger.valueOf(permitSignatureDto.getTokenAmount())
                .multiply(BigInteger.TEN.pow(smartContract.decimals().send().intValue()));
        BigInteger nonce = smartContract.nonces(permitSignatureDto.getUserAddress()).send();
        BigInteger deadline = BigInteger.valueOf(System.currentTimeMillis() / 1000 + 600); // 10 minutes from now

        return PermitSignatureDto.Response.builder()
                .domain(PermitSignatureDomain.builder()
                        .name(smartContractName)
                        .version(smartContractVersion)
                        .chainId(chainId)
                        .verifyingContract(permitSignatureDto.getSmartContractAddress())
                        .build()
                )
                .types(PermitSignatureTypes.builder().build())
                .message(PermitSignatureMessage.builder()
                        .owner(permitSignatureDto.getUserAddress())
                        .spender(permitSignatureDto.getSmartContractAddress())
                        .value(tokenAmountWei)
                        .nonce(nonce)
                        .deadline(deadline)
                        .build()
                )
                .build();
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public void trade(TradeDto tradeDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    tradeDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            // TODO: 거래 요청 로직 추가

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
