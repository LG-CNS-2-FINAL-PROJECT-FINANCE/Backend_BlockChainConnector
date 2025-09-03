package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deposit;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;
import com.ddiring.Backend_BlockchainConnector.domain.mapper.BlockchainLogMapper;
import com.ddiring.Backend_BlockchainConnector.domain.dto.trade.DepositDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.trade.TradeDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.PermitSignatureDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.domain.PermitSignatureDomain;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.message.PermitSignatureMessage;
import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;
import com.ddiring.Backend_BlockchainConnector.domain.mapper.DepositMapper;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.BlockchainLogRepository;
import com.ddiring.Backend_BlockchainConnector.repository.DepositRepository;
import com.ddiring.Backend_BlockchainConnector.repository.DeploymentRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
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
    private final KafkaMessageProducer kafkaMessageProducer;

    private final DeploymentRepository deploymentRepository;
    private final BlockchainLogRepository blockchainLogRepository;
    private final DepositRepository depositRepository;

    @Transactional
    public PermitSignatureDto.Response getSignature(@Valid PermitSignatureDto.Request permitSignatureDto) {
        try {
            Deployment contractInfo = deploymentRepository.findByProjectId(permitSignatureDto.getProjectId())
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

    @Transactional
    public void deposit(DepositDto depositDto) {
        if (depositRepository.existsBySellIdAndDepositType(depositDto.getSellId(), Deposit.DepositType.DEPOSIT)) {
            throw new EntityExistsException("이미 처리된 토큰 예치 요청입니다.");
        }

        try {
            Deployment contractInfo = deploymentRepository.findByProjectId(depositDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            Deposit deposit = DepositMapper.toEntity(contractInfo, depositDto, Deposit.DepositType.DEPOSIT);
            Deposit updatedDeposit = depositRepository.save(deposit);

            BlockchainLog blockchainLog = BlockchainLogMapper.toEntityForDeposit(contractInfo, updatedDeposit.getDepositId());
            blockchainLogRepository.save(blockchainLog);

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

                        BlockchainLog depositLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(depositDto.getProjectId(), updatedDeposit.getDepositId(), BlockchainRequestType.DEPOSIT)
                                .orElseThrow(() -> new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다."));
                        depositLog.updateSuccessResponse(response.getTransactionHash());
                        blockchainLogRepository.save(depositLog);

                        kafkaMessageProducer.sendDepositSucceededEvent(
                                depositDto.getProjectId(),
                                depositDto.getSellId(),
                                depositDto.getSellerAddress(),
                                depositDto.getTokenAmount().longValue()
                        );
                    })
                    .exceptionally(throwable -> {
                        log.error("[Smart Contract] 토큰 예치 요청 중 에러 발생 : {}", throwable.getMessage());

                        BlockchainLog depositLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(depositDto.getProjectId(), updatedDeposit.getDepositId(), BlockchainRequestType.DEPOSIT)
                                .orElseThrow(() -> new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다."));
                        depositLog.updateFailureResponse();
                        blockchainLogRepository.save(depositLog);

                        kafkaMessageProducer.sendDepositFailedEvent(
                                depositDto.getProjectId(),
                                depositDto.getSellId(),
                                depositDto.getSellerAddress(),
                                depositDto.getTokenAmount().longValue(),
                                throwable.getMessage()
                        );
                        return null;
                    });

        } catch (RuntimeException e) {
            log.error("[Deposit] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Deposit] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    @Transactional
    public void cancelDeposit(DepositDto cancelDepositDto) {
        if (depositRepository.existsBySellIdAndDepositType(cancelDepositDto.getSellId(), Deposit.DepositType.CANCEL_DEPOSIT)) {
            throw new EntityExistsException("이미 처리된 토큰 예치 취소 요청입니다.");
        }

        try {
            Deployment contractInfo = deploymentRepository.findByProjectId(cancelDepositDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            Deposit cancelDeposit = DepositMapper.toEntity(contractInfo, cancelDepositDto, Deposit.DepositType.CANCEL_DEPOSIT);
            Deposit updatedCancelDeposit = depositRepository.save(cancelDeposit);

            BlockchainLog blockchainLog = BlockchainLogMapper.toEntityForCancelDeposit(contractInfo, updatedCancelDeposit.getDepositId());
            blockchainLogRepository.save(blockchainLog);

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

                        BlockchainLog depositLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(cancelDepositDto.getProjectId(), updatedCancelDeposit.getDepositId(), BlockchainRequestType.CANCEL_DEPOSIT)
                                .orElseThrow(() -> new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다."));
                        depositLog.updateSuccessResponse(response.getTransactionHash());
                        blockchainLogRepository.save(depositLog);

                        kafkaMessageProducer.sendDepositCancelSucceededEvent(
                                cancelDepositDto.getProjectId(),
                                cancelDepositDto.getSellId(),
                                cancelDepositDto.getSellerAddress(),
                                cancelDepositDto.getTokenAmount().longValue()
                        );
                    }).exceptionally(throwable -> {
                        log.error("[Smart Contract] 토큰 예치 취소 요청 중 에러 발생 : {}", throwable.getMessage());

                        BlockchainLog depositLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(cancelDepositDto.getProjectId(), updatedCancelDeposit.getDepositId(), BlockchainRequestType.DEPOSIT)
                                .orElseThrow(() -> new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다."));
                        depositLog.updateFailureResponse();

                        kafkaMessageProducer.sendDepositCancelFailedEvent(
                                cancelDepositDto.getProjectId(),
                                cancelDepositDto.getSellId(),
                                cancelDepositDto.getSellerAddress(),
                                cancelDepositDto.getTokenAmount().longValue(),
                                throwable.getMessage()
                        );
                        return null;
                    });
        }
        catch (Exception e) {
            log.error("[CancelDeposit] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[CancelDeposit] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    @Transactional
    public void trade(TradeDto tradeDto) {
        if (blockchainLogRepository.existsByProjectIdAndOrderIdAndRequestType(tradeDto.getProjectId(), tradeDto.getTradeId(), BlockchainRequestType.TRADE)) {
            throw new EntityExistsException("이미 처리 중이거나 처리된 거래입니다.");
        }

        try {
            Deployment contractInfo = deploymentRepository.findByProjectId(tradeDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

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

                        BlockchainLog blockchainLog = BlockchainLogMapper.toEntityForTrade(contractInfo, response.getTransactionHash(), tradeDto.getTradeId());
                        blockchainLogRepository.save(blockchainLog);

                        kafkaMessageProducer.sendTradeRequestAcceptedEvent(
                                tradeDto.getProjectId(),
                                tradeDto.getTradeId(),
                                tradeDto.getBuyInfo().getBuyerAddress(),
                                tradeDto.getSellInfo().getSellerAddress(),
                                tradeDto.getTradeAmount()
                        );
                    })
                    .exceptionally(throwable -> {
                        log.error("[Smart Contract] 거래 요청 중 에러 발생: {}", throwable.getMessage());

                        kafkaMessageProducer.sendTradeRequestRejectedEvent(
                                tradeDto.getProjectId(),
                                tradeDto.getTradeId(),
                                tradeDto.getBuyInfo().getBuyerAddress(),
                                tradeDto.getSellInfo().getSellerAddress(),
                                tradeDto.getTradeAmount(),
                                throwable.getMessage()
                        );

                        return null;
                    });

        } catch (RuntimeException e) {
            log.error("[Trade] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Trade] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }
}
