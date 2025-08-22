package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.EventTrackerRepository;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProcessorService {
    private final EventTrackerRepository eventTrackerRepository;
    private final KafkaMessageProducer kafkaMessageProducer;

    @Transactional
    public void handleInvestmentSuccessful(FractionalInvestmentToken.InvestmentSuccessfulEventResponse event) {
        Long investmentId = Long.valueOf(event.investmentId);
        String buyerAddress = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository.findBySmartContractId_SmartContractAddress(event.log.getAddress());
        eventTracker.updateBlockNumber(event.log.getBlockNumber());
        eventTrackerRepository.save(eventTracker);

        log.info("[Investment 성공] 투자 번호 : {}, 투자자: {}, 금액: {}",
                investmentId, buyerAddress, tokenAmount);

        kafkaMessageProducer.sendInvestSucceededEvent(investmentId, buyerAddress, tokenAmount);
    }

    @Transactional
    public void handleInvestmentFailed(FractionalInvestmentToken.InvestmentFailedEventResponse event) {
        Long investmentId = Long.valueOf(event.investmentId);
        String buyerAddress = "buyerAddress";
        Long tokenAmount = 100L;

        EventTracker eventTracker = eventTrackerRepository.findBySmartContractId_SmartContractAddress(event.log.getAddress());
        eventTracker.updateBlockNumber(event.log.getBlockNumber());
        eventTrackerRepository.save(eventTracker);

        log.info("[Investment 실패] 프로젝트 번호 : {}, 사유: {}", event.projectId, event.reason);

        kafkaMessageProducer.sendInvestFailedEvent(investmentId, buyerAddress, tokenAmount, event.reason);
    }

    @Transactional
    public void handleTradeSuccessful(FractionalInvestmentToken.TradeSuccessfulEventResponse event) {
        Long tradeId = Long.valueOf(event.tradeId);
        String seller = event.seller;
        String buyer = event.buyer;
        Long tokenAmount = event.tokenAmount.longValue();

        EventTracker eventTracker = eventTrackerRepository.findBySmartContractId_SmartContractAddress(event.log.getAddress());
        eventTracker.updateBlockNumber(event.log.getBlockNumber());
        eventTrackerRepository.save(eventTracker);

        log.info("[Trade 성공] 거래 번호: {}, 판매자: {}, 구매자: {}, 금액: {}",
                Arrays.toString(event.projectId),
                event.seller,
                event.buyer,
                event.tokenAmount
        );

        kafkaMessageProducer.sendTradeSucceededEvent(tradeId, buyer, tokenAmount, seller, tokenAmount);
    }

    @Transactional
    public void handleTradeFailed(FractionalInvestmentToken.TradeFailedEventResponse event) {
        Long tradeId = Long.valueOf(event.tradeId);
        String buyer = "buyerAddress";
        String seller = "sellerAddress";
        Long tokenAmount = 100L;

        EventTracker eventTracker = eventTrackerRepository.findBySmartContractId_SmartContractAddress(event.log.getAddress());
        eventTracker.updateBlockNumber(event.log.getBlockNumber());
        eventTrackerRepository.save(eventTracker);

        log.info("[Trade 실패] 거래 번호: {}, 사유: {}", event.projectId, event.reason);

        kafkaMessageProducer.sendTradeFailedEvent(tradeId, buyer, tokenAmount, seller, tokenAmount, event.reason);
    }
}
