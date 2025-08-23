package com.ddiring.Backend_BlockchainConnector.event.producer;

import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeployFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeploySucceededEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.investment.InvestFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.investment.InvestRequestAcceptedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.investment.InvestRequestRejectedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.investment.InvestSucceededEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.trade.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendDeploySucceededEvent(String projectId) {
        DeploySucceededEvent message = DeploySucceededEvent.of(projectId);
        log.info("Sending DeploySucceededEvent to topic {}: {}", DeploySucceededEvent.TOPIC, message);
        sendMessage(DeploySucceededEvent.TOPIC, message);
    }

    public void sendDeployFailedEvent(String projectId, String errorMessage) {
        DeployFailedEvent message = DeployFailedEvent.of(projectId, errorMessage);
        log.info("Sending DeployFailedEvent to topic {}: {}", DeployFailedEvent.TOPIC, message);
        sendMessage(DeployFailedEvent.TOPIC, message);
    }

    public void sendInvestRequestAcceptedEvent(Long investmentId) {
        InvestRequestAcceptedEvent message = InvestRequestAcceptedEvent.of(investmentId);
        log.info("Sending InvestRequestAcceptedEvent to topic {}: {}", InvestRequestAcceptedEvent.TOPIC, message);
        sendMessage(InvestRequestAcceptedEvent.TOPIC, message);
    }

    public void sendInvestRequestRejectedEvent(Long investmentId, String errorMessage) {
        InvestRequestRejectedEvent message = InvestRequestRejectedEvent.of(investmentId, errorMessage);
        log.info("Sending InvestRequestRejectedEvent to topic {}: {}", InvestRequestRejectedEvent.TOPIC, message);
        sendMessage(InvestRequestRejectedEvent.TOPIC, message);
    }

    public void sendInvestSucceededEvent(Long investmentId, String buyerAddress, Long tokenAmount) {
        InvestSucceededEvent message = InvestSucceededEvent.of(investmentId, buyerAddress, tokenAmount);
        log.info("Sending InvestSucceededEvent to topic {}: {}", InvestSucceededEvent.TOPIC, message);
        sendMessage(InvestSucceededEvent.TOPIC, message);
    }

    public void sendInvestFailedEvent(Long investmentId, String errorType, String errorMessage) {
        InvestFailedEvent message = InvestFailedEvent.of(investmentId, errorType, errorMessage);
        log.info("Sending InvestFailedEvent to topic {}: {}", InvestFailedEvent.TOPIC, message);
        sendMessage(InvestFailedEvent.TOPIC, message);
    }

    public void sendDepositSucceededEvent(Long sellId, String sellerAddress, Long tokenAmount) {
        DepositSucceededEvent message = DepositSucceededEvent.of(sellId, sellerAddress, tokenAmount);
        log.info("Sending DepositSucceededEvent to topic {}: {}", DepositSucceededEvent.TOPIC, message);
        sendMessage(DepositSucceededEvent.TOPIC, message);
    }

    public void sendDepositFailedEvent(Long sellId, String sellerAddress, Long tokenAmount, String errorMessage) {
        DepositFailedEvent message = DepositFailedEvent.of(sellId, sellerAddress, tokenAmount, errorMessage);
        log.info("Sending DepositFailedEvent to topic {}: {}", DepositFailedEvent.TOPIC, message);
        sendMessage(DepositFailedEvent.TOPIC, message);
    }

    public void sendTradeRequestAcceptedEvent(Long tradeId) {
        TradeRequestAcceptedEvent message = TradeRequestAcceptedEvent.of(tradeId);
        log.info("Sending TradeRequestAcceptedEvent to topic {}: {}", TradeRequestAcceptedEvent.TOPIC, message);
        sendMessage(TradeRequestAcceptedEvent.TOPIC, message);
    }

    public void sendTradeRequestRejectedEvent(Long tradeId, String errorMessage) {
        TradeRequestRejectedEvent message = TradeRequestRejectedEvent.of(tradeId, errorMessage);
        log.info("Sending TradeRequestRejectedEvent to topic {}: {}", TradeRequestRejectedEvent.TOPIC, message);
        sendMessage(TradeRequestRejectedEvent.TOPIC, message);
    }

    public void sendTradeSucceededEvent(Long tradeId, String buyerAddress, Long buyerTokenAmount, String sellerAddress, Long sellerTokenAmount) {
        TradeSucceededEvent message = TradeSucceededEvent.of(tradeId, buyerAddress, buyerTokenAmount, sellerAddress, sellerTokenAmount);
        log.info("Sending TradeSucceededEvent to topic {}: {}", TradeSucceededEvent.TOPIC, message);
        sendMessage(TradeSucceededEvent.TOPIC, message);
    }

    public void sendTradeFailedEvent(Long tradeId, String errorType, String errorMessage) {
        TradeFailedEvent message = TradeFailedEvent.of(tradeId, errorType, errorMessage);
        log.info("Sending TradeFailedEvent to topic {}: {}", TradeFailedEvent.TOPIC, message);
        sendMessage(TradeFailedEvent.TOPIC, message);
    }
}
