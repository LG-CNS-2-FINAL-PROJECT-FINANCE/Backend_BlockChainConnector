package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.dto.DepositWithPermitDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.TradeDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.PermitSignatureDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain.PermitSignatureDomain;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.message.PermitSignatureMessage;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

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

    public void deposit(DepositWithPermitDto depositDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    depositDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            smartContract.depositWithPermit(
                    depositDto.getTradeId().toString(),
                    depositDto.getSellerAddress(),
                    depositDto.getBuyerAddress(),
                    depositDto.getTokenAmount(),
                    depositDto.getDeadline(),
                    BigInteger.valueOf(depositDto.getV()),
                    Numeric.hexStringToByteArray(depositDto.getR()),
                    Numeric.hexStringToByteArray(depositDto.getS())
            ).sendAsync()
                .thenAccept(response -> {
                    // TODO; 거래 성공 후 처리 로직 추가
                    // 예: 거래 성공 이벤트 발생, DB 업데이트 등
                    log.info("[Smart Contract] 거래 성공: {}", response);
                })
                .exceptionally(throwable -> {
                    log.error("[Smart Contract] 토큰 예치 요청 중 에러 발생 : {}", throwable.getMessage());
                    throw new RuntimeException("[Smart Contract] 토큰 예치 요청 중 에러 발생 : " + throwable.getMessage());
                });
        } catch (Exception e) {
            log.error("[Blockchain Connector]   : {}", e.getMessage());
            throw new RuntimeException("[Blockchain Connector] 예상치 못한 에러 발생 : " + e.getMessage());
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

            smartContract.requestTrade(
                    tradeDto.getTradeId().toString(),
                    tradeDto.getSellerAddress(),
                    tradeDto.getBuyerAddress(),
                    BigInteger.valueOf(tradeDto.getTokenAmount())
            ).sendAsync()
                .thenAccept(response -> {
                    log.info("[Smart Contract] 거래 요청 성공: {}", response);
                })
                .exceptionally(throwable -> {
                    log.error("[Smart Contract] 거래 요청 중 에러 발생: {}", throwable.getMessage());
                    throw new RuntimeException("[Smart Contract] 거래 요청 중 에러 발생: " + throwable.getMessage());
                });

        } catch (Exception e) {
            throw new RuntimeException("[Blockchain Connector] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }
}
