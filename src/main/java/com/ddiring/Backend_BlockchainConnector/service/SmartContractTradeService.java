package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.domain.mapper.BlockchainLogMapper;
import com.ddiring.Backend_BlockchainConnector.domain.dto.trade.DepositWithPermitDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.trade.TradeDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.PermitSignatureDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain.PermitSignatureDomain;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.message.PermitSignatureMessage;
import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.BlockchainLogRepository;
import com.ddiring.Backend_BlockchainConnector.repository.SmartContractRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractTradeService {
    private final ContractWrapper contractWrapper;
    private final KafkaMessageProducer kafkaMessageProducer;

    private final SmartContractRepository smartContractRepository;
    private final BlockchainLogRepository blockchainLogRepository;

    @Transactional
    public PermitSignatureDto.Response getSignature(@Valid PermitSignatureDto.Request permitSignatureDto) {
        try {
            SmartContract contractInfo = smartContractRepository.findByProjectId(permitSignatureDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

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
                        .verifyingContract(contractInfo.getSmartContractAddress())
                        .build()
                )
                .message(PermitSignatureMessage.builder()
                        .owner(permitSignatureDto.getUserAddress())
                        .spender(contractInfo.getSmartContractAddress())
                        .value(tokenAmountWei)
                        .nonce(nonce)
                        .deadline(deadline)
                        .build()
                )
                .build();
        } catch (Exception e) {
            log.error("[Signature] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Signature] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public void deposit(DepositWithPermitDto depositDto) {
        try {
            SmartContract contractInfo = smartContractRepository.findByProjectId(depositDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            AtomicReference<BlockchainLog> blockchainLog = new AtomicReference<>();

            smartContract.depositWithPermit(
                            depositDto.getSellId().toString(),
                            depositDto.getSellerAddress(),
                            depositDto.getTokenAmount(),
                            depositDto.getDeadline(),
                            BigInteger.valueOf(depositDto.getV()),
                            Numeric.hexStringToByteArray(depositDto.getR()),
                            Numeric.hexStringToByteArray(depositDto.getS())
                    ).sendAsync()
                    .thenAccept(response -> {
                        log.info("[Smart Contract] 예금 성공: {}", response);

                        blockchainLog.set(BlockchainLogMapper.toEntityForDepositSucceeded(contractInfo, response.getTransactionHash()));

                        kafkaMessageProducer.sendDepositSucceededEvent(
                                depositDto.getSellId(),
                                depositDto.getSellerAddress(),
                                depositDto.getTokenAmount().longValue()
                        );
                    })
                    .exceptionally(throwable -> {
                        log.error("[Smart Contract] 토큰 예치 요청 중 에러 발생 : {}", throwable.getMessage());

                        blockchainLog.set(BlockchainLogMapper.toEntityForDepositFailed(contractInfo));

                        kafkaMessageProducer.sendDepositFailedEvent(
                                depositDto.getSellId(),
                                depositDto.getSellerAddress(),
                                depositDto.getTokenAmount().longValue(),
                                throwable.getMessage()
                        );
                        return null;
                    });

            blockchainLogRepository.save(blockchainLog.get());

        } catch (RuntimeException e) {
            log.error("[Deposit] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Deposit] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public void cancelDeposit(DepositWithPermitDto cancelDepositDto) {
        try {
            SmartContract contractInfo = smartContractRepository.findByProjectId(cancelDepositDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            AtomicReference<BlockchainLog> blockchainLog = new AtomicReference<>();

            smartContract.cancelDeposit(
                            cancelDepositDto.getSellId().toString(),
                            cancelDepositDto.getSellerAddress(),
                            cancelDepositDto.getTokenAmount(),
                            cancelDepositDto.getDeadline(),
                            BigInteger.valueOf(cancelDepositDto.getV()),
                            Numeric.hexStringToByteArray(cancelDepositDto.getR()),
                            Numeric.hexStringToByteArray(cancelDepositDto.getS())
                    ).sendAsync()
                    .thenAccept(response -> {
                        log.info("[Smart Contract] 예금 취소 성공: {}", response);

                        blockchainLog.set(BlockchainLogMapper.toEntityForCancelDepositSucceeded(contractInfo, response.getTransactionHash()));

                        kafkaMessageProducer.sendDepositCancelSucceededEvent(
                                cancelDepositDto.getSellId(),
                                cancelDepositDto.getSellerAddress(),
                                cancelDepositDto.getTokenAmount().longValue()
                        );
                        // TODO: 예금 취소 기록 DB 저장
                    }).exceptionally(throwable -> {
                        log.error("[Smart Contract] 토큰 예치 취소 요청 중 에러 발생 : {}", throwable.getMessage());

                        blockchainLog.set(BlockchainLogMapper.toEntityForCancelDepositFailed(contractInfo));

                        kafkaMessageProducer.sendDepositCancelFailedEvent(
                                cancelDepositDto.getSellId(),
                                cancelDepositDto.getSellerAddress(),
                                cancelDepositDto.getTokenAmount().longValue(),
                                throwable.getMessage()
                        );
                        return null;
                    });

            blockchainLogRepository.save(blockchainLog.get());
        }
        catch (Exception e) {
            log.error("[CancelDeposit] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[CancelDeposit] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public void trade(TradeDto tradeDto) {
        try {
            SmartContract contractInfo = smartContractRepository.findByProjectId(tradeDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            AtomicReference<BlockchainLog> blockchainLog = new AtomicReference<>();

            smartContract.requestTrade(
                            tradeDto.getTradeId().toString(),
                            tradeDto.getSellInfo().getSellId().toString(),
                            tradeDto.getSellInfo().getSellerAddress(),
                            tradeDto.getBuyInfo().getBuyId().toString(),
                            tradeDto.getBuyInfo().getBuyerAddress(),
                            BigInteger.valueOf(tradeDto.getTradeAmount()),
                            BigInteger.valueOf(tradeDto.getPricePerToken())
                    ).sendAsync()
                    .thenAccept(response -> {
                        log.info("[Smart Contract] 거래 요청 성공: {}", response);

                        blockchainLog.set(BlockchainLogMapper.toEntityForTrade(contractInfo, response.getTransactionHash()));

                        kafkaMessageProducer.sendTradeRequestAcceptedEvent(tradeDto.getTradeId());
                    })
                    .exceptionally(throwable -> {
                        log.error("[Smart Contract] 거래 요청 중 에러 발생: {}", throwable.getMessage());
                        kafkaMessageProducer.sendTradeRequestRejectedEvent(tradeDto.getTradeId(), throwable.getMessage());
                        return null;
                    });

            blockchainLogRepository.save(blockchainLog.get());

        } catch (RuntimeException e) {
            log.error("[Trade] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Trade] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }
}
