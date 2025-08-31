package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventErrorType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.EventTrackerRepository;
import com.ddiring.Backend_BlockchainConnector.repository.BlockchainLogRepository;
import com.ddiring.Backend_BlockchainConnector.repository.SmartContractRepository;
import com.ddiring.Backend_BlockchainConnector.utils.Byte32Converter;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractEventProcessorService {
    private final KafkaMessageProducer kafkaMessageProducer;

    private final SmartContractRepository smartContractRepository;
    private final EventTrackerRepository eventTrackerRepository;
    private final BlockchainLogRepository blockchainLogRepository;

    @Transactional
    public void handleInvestmentSuccessful(FractionalInvestmentToken.InvestmentSuccessfulEventResponse event) {
        Long investmentId = Long.valueOf(event.investmentId);
        String buyerAddress = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndSmartContractId_SmartContractAddress(OracleEventType.INVESTMENT_SUCCESSFUL, event.log.getAddress())
                .orElseThrow(() -> new NotFound("INVESTMENT_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        String strProjectId = Byte32Converter.convertBytes32ToString(event.projectId);
        log.info("[Investment 성공] 프로젝트 번호: {}, 투자 번호 : {}, 투자자: {}, 금액: {}", strProjectId, investmentId, buyerAddress, tokenAmount);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectIdAndOrderId(strProjectId, Long.valueOf(event.investmentId))
                .orElseThrow(() -> new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다."));
        blockchainLog.updateInvestmentSucceeded(event.log.getTransactionHash());
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendInvestSucceededEvent(investmentId, buyerAddress, tokenAmount);
    }

    @Transactional
    public void handleInvestmentFailed(FractionalInvestmentToken.InvestmentFailedEventResponse event) {
        if (OracleEventErrorType.REPEAT_FAILED.equals(OracleEventErrorType.fromValue(event.status.longValue()))) {
            log.info("[InvestmentFailed] Request already processed.");
        }

        Long investmentId = Long.valueOf(event.investmentId);

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndSmartContractId_SmartContractAddress(OracleEventType.INVESTMENT_FAILED, event.log.getAddress())
                .orElseThrow(() -> new NotFound("INVESTMENT_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        String strProjectId = Byte32Converter.convertBytes32ToString(event.projectId);
        log.info("[Investment 실패] 프로젝트 번호: {}, 투자 번호 : {}, 사유: {}, 상태 코드: {}", strProjectId, investmentId, event.reason, event.status);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));


        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectIdAndOrderId(strProjectId, Long.valueOf(event.investmentId))
                .orElseThrow(() -> new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다."));
        blockchainLog.updateInvestmentFailed(event.log.getTransactionHash(), OracleEventErrorType.fromValue(event.status.longValue()), event.reason);
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendInvestFailedEvent(investmentId, blockchainLog.getOracleEventType().name(), event.reason);
    }

    @Transactional
    public void handleTradeSuccessful(FractionalInvestmentToken.TradeSuccessfulEventResponse event) {
        Long tradeId = Long.valueOf(event.tradeId);
        String seller = event.seller;
        String buyer = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndSmartContractId_SmartContractAddress(OracleEventType.TRADE_SUCCESSFUL, event.log.getAddress())
                .orElseThrow(() -> new NotFound("TRADE_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        log.info("[Trade 성공] 프로젝트 번호 : {}, 거래 번호: {}, 판매자: {}, 구매자: {}, 금액: {}", Byte32Converter.convertBytes32ToString(event.projectId), tradeId, seller, buyer, tokenAmount);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        BlockchainLog blockchainLog = BlockchainLog.builder()
                .smartContract(smartContract)
                .oracleTransactionHash(event.log.getTransactionHash())
                .oracleEventType(OracleEventType.TRADE_SUCCESSFUL)
                .requestStatus(BlockchainRequestStatus.SUCCESS)
                .build();

        // TODO: 거래 기록 DB 저장 (성공)
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendTradeSucceededEvent(tradeId, buyer, tokenAmount, seller, tokenAmount);
    }

    @Transactional
    public void handleTradeFailed(FractionalInvestmentToken.TradeFailedEventResponse event) {
        if (OracleEventErrorType.REPEAT_FAILED.equals(OracleEventErrorType.fromValue(event.status.longValue()))) {
            log.info("[TradeFailed] Request already processed.");
        }

        Long tradeId = Long.valueOf(event.tradeId);

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndSmartContractId_SmartContractAddress(OracleEventType.TRADE_FAILED, event.log.getAddress())
                .orElseThrow(() -> new NotFound("TRADE_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        log.info("[Trade 실패] 프로젝트 번호: {}, 거래 번호 : {}, 사유: {}, 상태 코드: {}", Byte32Converter.convertBytes32ToString(event.projectId), tradeId, event.reason, event.status);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        BlockchainLog blockchainLog = BlockchainLog.builder()
                .smartContract(smartContract)
                .oracleTransactionHash(event.log.getTransactionHash())
                .oracleEventType(OracleEventType.TRADE_FAILED)
                .requestStatus(BlockchainRequestStatus.FAILURE)
                .errorType(OracleEventErrorType.fromValue(event.status.longValue()))
                .errorReason(event.reason)
                .build();

        // TODO: 거래 기록 DB 저장 (실패)
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendTradeFailedEvent(tradeId, blockchainLog.getErrorType().name(), event.reason);
    }
}
