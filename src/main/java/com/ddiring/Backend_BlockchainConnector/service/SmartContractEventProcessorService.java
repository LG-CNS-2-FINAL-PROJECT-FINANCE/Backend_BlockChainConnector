package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventErrorType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventType;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.EventTrackerRepository;
import com.ddiring.Backend_BlockchainConnector.repository.BlockchainLogRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
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
    private final ContractWrapper contractWrapper;

    private final KafkaMessageProducer kafkaMessageProducer;

    private final EventTrackerRepository eventTrackerRepository;
    private final BlockchainLogRepository blockchainLogRepository;

    @Transactional
    public void handleInvestmentSuccessful(FractionalInvestmentToken.InvestmentSuccessfulEventResponse event) {
        Long investmentId = Long.valueOf(event.investmentId);
        String strProjectId = Byte32Converter.convertBytes32ToString(event.projectId);

        // 1. 블록체인 로그 엔티티 조회 (DB 저장 이전 상태)
        BlockchainLog blockchainLog = blockchainLogRepository
                .findByProjectIdAndOrderIdAndRequestType(strProjectId, investmentId, BlockchainRequestType.INVESTMENT)
                .orElseGet(() -> {
                    log.error("[Investment 성공] 매칭되는 블록체인 기록을 찾을 수 없습니다. projectId: {}, orderId: {}", strProjectId, investmentId);
                    throw new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다.");
                });

        try {
            EventTracker eventTracker = eventTrackerRepository
                    .findByOracleEventTypeAndDeploymentId_SmartContractAddress(OracleEventType.INVESTMENT_SUCCESSFUL, event.log.getAddress())
                    .orElseGet(() -> {
                        log.error("[Investment 성공] INVESTMENT_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다. Address: {}", event.log.getAddress());
                        throw new NotFound("INVESTMENT_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다.");
                    });
            eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE));
            eventTrackerRepository.save(eventTracker);

            FractionalInvestmentToken contract = contractWrapper.getSmartContract(event.log.getAddress());
            Long minInvestmentAmount = contract.minInvestmentAmount().send().longValue();

            blockchainLog.updateOracleSuccessResponse(OracleEventType.INVESTMENT_SUCCESSFUL, event.log.getTransactionHash());
            blockchainLogRepository.save(blockchainLog);

            kafkaMessageProducer.sendInvestSucceededEvent(strProjectId, investmentId, event.buyer, event.tokenAmount.longValue(), minInvestmentAmount);

            log.info("[Investment 성공] 프로젝트 번호: {}, 투자 번호 : {}, 투자자: {}, 금액: {}", strProjectId, investmentId, event.buyer, event.tokenAmount.longValue());

        } catch (Exception e) {
            log.error("[Investment 실패] 블록체인 이벤트 처리 중 오류 발생: {}", e.getMessage());

            // 6. 블록체인 로그 상태 업데이트 (실패)
            blockchainLog.updateOracleFailureResponse(
                    OracleEventType.INVESTMENT_FAILED,
                    event.log.getTransactionHash(),
                    OracleEventErrorType.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
            blockchainLogRepository.save(blockchainLog);

            throw new RuntimeException("블록체인 이벤트 처리 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public void handleInvestmentFailed(FractionalInvestmentToken.InvestmentFailedEventResponse event) {
        if (OracleEventErrorType.REPEAT_FAILED.equals(OracleEventErrorType.fromValue(event.status.longValue()))) {
            log.info("[InvestmentFailed] Request already processed.");
        }

        Long investmentId = Long.valueOf(event.investmentId);

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndDeploymentId_SmartContractAddress(OracleEventType.INVESTMENT_FAILED, event.log.getAddress())
                .orElseGet(() -> {
                    log.error("[Investment 실패] INVESTMENT_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다. Address: {}", event.log.getAddress());
                    throw new NotFound("INVESTMENT_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다.");
                });
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        String strProjectId = Byte32Converter.convertBytes32ToString(event.projectId);
        log.info("[Investment 실패] 프로젝트 번호: {}, 투자 번호 : {}, 사유: {}, 상태 코드: {}", strProjectId, investmentId, event.reason, event.status);

        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(strProjectId, Long.valueOf(event.investmentId), BlockchainRequestType.INVESTMENT)
                .orElseGet(() -> {
                    log.error("[Investment 실패] 매칭되는 블록체인 기록을 찾을 수 없습니다. projectId: {}, orderId: {}", strProjectId, investmentId);
                    throw new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다.");
                });
        blockchainLog.updateOracleFailureResponse(OracleEventType.INVESTMENT_FAILED, event.log.getTransactionHash(), OracleEventErrorType.fromValue(event.status.longValue()), event.reason);
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendInvestFailedEvent(strProjectId, investmentId, blockchainLog.getOracleEventType().name(), event.reason);
    }

    @Transactional
    public void handleTradeSuccessful(FractionalInvestmentToken.TradeSuccessfulEventResponse event) {
        Long tradeId = Long.valueOf(event.tradeId);
        String seller = event.seller;
        String buyer = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndDeploymentId_SmartContractAddress(OracleEventType.TRADE_SUCCESSFUL, event.log.getAddress())
                .orElseGet(() -> {
                    log.error("[Trade 실패] TRADE_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다. Address: {}", event.log.getAddress());
                    throw new NotFound("TRADE_SUCCESSFUL에 매핑되는 EventTracker를 찾을 수 없습니다.");
                });
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        String strProjectId = Byte32Converter.convertBytes32ToString(event.projectId);
        log.info("[Trade 성공] 프로젝트 번호 : {}, 거래 번호: {}, 판매자: {}, 구매자: {}, 금액: {}", Byte32Converter.convertBytes32ToString(event.projectId), tradeId, seller, buyer, tokenAmount);

        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(strProjectId, Long.valueOf(event.tradeId), BlockchainRequestType.TRADE)
                .orElseGet(() -> {
                    log.error("[Trade 성공] 매칭되는 블록체인 기록을 찾을 수 없습니다. projectId: {}, orderId: {}", strProjectId, tradeId);
                    throw new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다.");
                });
        blockchainLog.updateOracleSuccessResponse(OracleEventType.TRADE_SUCCESSFUL, event.log.getTransactionHash());
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendTradeSucceededEvent(strProjectId, tradeId, buyer, tokenAmount, seller, tokenAmount);
    }

    @Transactional
    public void handleTradeFailed(FractionalInvestmentToken.TradeFailedEventResponse event) {
        if (OracleEventErrorType.REPEAT_FAILED.equals(OracleEventErrorType.fromValue(event.status.longValue()))) {
            log.info("[TradeFailed] Request already processed.");
        }

        Long tradeId = Long.valueOf(event.tradeId);

        EventTracker eventTracker = eventTrackerRepository
                .findByOracleEventTypeAndDeploymentId_SmartContractAddress(OracleEventType.TRADE_FAILED, event.log.getAddress())
                .orElseGet(() -> {
                    log.error("[Trade 실패] TRADE_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다. Address: {}", event.log.getAddress());
                    throw new NotFound("TRADE_FAILED에 매핑되는 EventTracker를 찾을 수 없습니다.");
                });
        eventTracker.updateBlockNumber(event.log.getBlockNumber().add(BigInteger.ONE)); // 현재 블록의 다음 번호부터 이벤트 리스닝
        eventTrackerRepository.save(eventTracker);

        String strProjectId = Byte32Converter.convertBytes32ToString(event.projectId);
        log.info("[Trade 실패] 프로젝트 번호: {}, 거래 번호 : {}, 사유: {}, 상태 코드: {}", Byte32Converter.convertBytes32ToString(event.projectId), tradeId, event.reason, event.status);

        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectIdAndOrderIdAndRequestType(strProjectId, Long.valueOf(event.tradeId), BlockchainRequestType.TRADE)
                .orElseGet(() -> {
                    log.error("[Trade 실패] 매칭되는 블록체인 기록을 찾을 수 없습니다. projectId: {}, orderId: {}", strProjectId, tradeId);
                    throw new NotFound("매칭되는 블록체인 기록을 찾을 수 없습니다.");
                });
        blockchainLog.updateOracleFailureResponse(OracleEventType.TRADE_FAILED, event.log.getTransactionHash(), OracleEventErrorType.fromValue(event.status.longValue()), event.reason);
        blockchainLogRepository.save(blockchainLog);

        kafkaMessageProducer.sendTradeFailedEvent(strProjectId, tradeId, blockchainLog.getErrorType().name(), event.reason);
    }
}
