package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTransactionLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.domain.enums.EventErrorType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.EventType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.TransactionResult;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.EventTrackerRepository;
import com.ddiring.Backend_BlockchainConnector.repository.EventTransactionLogRepository;
import com.ddiring.Backend_BlockchainConnector.repository.SmartContractRepository;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractEventProcessorService {
    private final KafkaMessageProducer kafkaMessageProducer;

    private final SmartContractRepository smartContractRepository;
    private final EventTrackerRepository eventTrackerRepository;
    private final EventTransactionLogRepository eventTransactionLogRepository;

    @Transactional
    public void handleInvestmentSuccessful(FractionalInvestmentToken.InvestmentSuccessfulEventResponse event) {
        String transactionHash = event.log.getTransactionHash();
        if (eventTransactionLogRepository.existsByTransactionHash(transactionHash)) {
            log.warn("[InvestmentSuccess] Duplicate transaction hash detected. Skipping save for hash: {}", transactionHash);
            return;
        }

        Long investmentId = Long.valueOf(event.investmentId);
        String buyerAddress = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository
                .findByEventTypeAndSmartContractId_SmartContractAddress(EventType.INVESTMENT_SUCCESSFUL, event.log.getAddress())
                .orElseThrow(() -> new NotFound("INVESTMENT_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        log.info("[Investment 성공] 프로젝트 번호: {}, 투자 번호 : {}, 투자자: {}, 금액: {}", event.projectId, investmentId, buyerAddress, tokenAmount);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        EventTransactionLog eventTransactionLog = EventTransactionLog.builder()
                .smartContractId(smartContract)
                .transactionHash(transactionHash)
                .eventType(EventType.INVESTMENT_SUCCESSFUL)
                .transactionResult(TransactionResult.SUCCESS)
                .build();

        eventTransactionLogRepository.save(eventTransactionLog);

        kafkaMessageProducer.sendInvestSucceededEvent(investmentId, buyerAddress, tokenAmount);
    }

    @Transactional
    public void handleInvestmentFailed(FractionalInvestmentToken.InvestmentFailedEventResponse event) {
        if (EventErrorType.REPEAT_FAILED.equals(EventErrorType.fromValue(event.status.longValue()))) {
            log.info("[InvestmentFailed] Request already processed.");
        }

        String transactionHash = event.log.getTransactionHash();
        if (eventTransactionLogRepository.existsByTransactionHash(transactionHash)) {
            log.warn("[InvestmentFailed] Duplicate transaction hash detected. Skipping save for hash: {}", transactionHash);
            return;
        }

        Long investmentId = Long.valueOf(event.investmentId);

        EventTracker eventTracker = eventTrackerRepository
                .findByEventTypeAndSmartContractId_SmartContractAddress(EventType.INVESTMENT_FAILED, event.log.getAddress())
                .orElseThrow(() -> new NotFound("INVESTMENT_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        log.info("[Investment 실패] 프로젝트 번호: {}, 투자 번호 : {}, 사유: {}, 상태 코드: {}", event.projectId, investmentId, event.reason, event.status);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        EventTransactionLog eventTransactionLog = EventTransactionLog.builder()
                .smartContractId(smartContract)
                .transactionHash(event.log.getTransactionHash())
                .eventType(EventType.INVESTMENT_FAILED)
                .transactionResult(TransactionResult.FAILURE)
                .errorType(EventErrorType.fromValue(event.status.longValue()))
                .errorReason(event.reason)
                .build();

        eventTransactionLogRepository.save(eventTransactionLog);

        kafkaMessageProducer.sendInvestFailedEvent(investmentId,eventTransactionLog.getEventType().name(), event.reason);
    }

    @Transactional
    public void handleTradeSuccessful(FractionalInvestmentToken.TradeSuccessfulEventResponse event) {
        String transactionHash = event.log.getTransactionHash();
        if (eventTransactionLogRepository.existsByTransactionHash(transactionHash)) {
            log.warn("[TradeSuccess] Duplicate transaction hash detected. Skipping save for hash: {}", transactionHash);
            return;
        }

        Long tradeId = Long.valueOf(event.tradeId);
        String seller = event.seller;
        String buyer = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository
                .findByEventTypeAndSmartContractId_SmartContractAddress(EventType.TRADE_SUCCESSFUL, event.log.getAddress())
                .orElseThrow(() -> new NotFound("TRADE_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        log.info("[Trade 성공] 프로젝트 번호 : {}, 거래 번호: {}, 판매자: {}, 구매자: {}, 금액: {}", event.projectId, tradeId, seller, buyer, tokenAmount);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        EventTransactionLog eventTransactionLog = EventTransactionLog.builder()
                .smartContractId(smartContract)
                .transactionHash(event.log.getTransactionHash())
                .eventType(EventType.TRADE_SUCCESSFUL)
                .transactionResult(TransactionResult.SUCCESS)
                .build();

        eventTransactionLogRepository.save(eventTransactionLog);

        kafkaMessageProducer.sendTradeSucceededEvent(tradeId, buyer, tokenAmount, seller, tokenAmount);
    }

    @Transactional
    public void handleTradeFailed(FractionalInvestmentToken.TradeFailedEventResponse event) {
        if (EventErrorType.REPEAT_FAILED.equals(EventErrorType.fromValue(event.status.longValue()))) {
            log.info("[TradeFailed] Request already processed.");
        }

        String transactionHash = event.log.getTransactionHash();
        if (eventTransactionLogRepository.existsByTransactionHash(transactionHash)) {
            log.warn("[TradeFailed] Duplicate transaction hash detected. Skipping save for hash: {}", transactionHash);
            return;
        }

        Long tradeId = Long.valueOf(event.tradeId);

        EventTracker eventTracker = eventTrackerRepository
                .findByEventTypeAndSmartContractId_SmartContractAddress(EventType.TRADE_FAILED, event.log.getAddress())
                .orElseThrow(() -> new NotFound("TRADE_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다."));
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        log.info("[Trade 실패] 프로젝트 번호: {}, 거래 번호 : {}, 사유: {}, 상태 코드: {}", event.projectId, tradeId, event.reason, event.status);

        SmartContract smartContract = smartContractRepository.findBySmartContractAddress(event.log.getAddress())
                .orElseThrow(() -> new NotFound("해당 컨트랙트 주소는 존재하지 않습니다."));

        EventTransactionLog eventTransactionLog = EventTransactionLog.builder()
                .smartContractId(smartContract)
                .transactionHash(event.log.getTransactionHash())
                .eventType(EventType.TRADE_FAILED)
                .transactionResult(TransactionResult.FAILURE)
                .errorType(EventErrorType.fromValue(event.status.longValue()))
                .errorReason(event.reason)
                .build();

        eventTransactionLogRepository.save(eventTransactionLog);

        kafkaMessageProducer.sendTradeFailedEvent(tradeId, eventTransactionLog.getErrorType().name(), event.reason);
    }
}
