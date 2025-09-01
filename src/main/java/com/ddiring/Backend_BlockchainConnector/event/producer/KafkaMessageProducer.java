package com.ddiring.Backend_BlockchainConnector.event.producer;

import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeployFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeploySucceededEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deposit.DepositCancelFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deposit.DepositCancelSucceededEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deposit.DepositFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deposit.DepositSucceededEvent;
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

    public void sendInvestRequestAcceptedEvent(String projectId) {
        InvestRequestAcceptedEvent message = InvestRequestAcceptedEvent.of(projectId);
        log.info("Sending InvestRequestAcceptedEvent to topic {}: {}", InvestRequestAcceptedEvent.TOPIC, message);
        sendMessage(InvestRequestAcceptedEvent.TOPIC, message);
    }

    public void sendInvestRequestRejectedEvent(String projectId, String errorMessage) {
        InvestRequestRejectedEvent message = InvestRequestRejectedEvent.of(projectId, errorMessage);
        log.info("Sending InvestRequestRejectedEvent to topic {}: {}", InvestRequestRejectedEvent.TOPIC, message);
        sendMessage(InvestRequestRejectedEvent.TOPIC, message);
    }

    public void sendInvestSucceededEvent(String projectId, Long investmentId, String buyerAddress, Long tokenAmount, Long initialAmountPerToken) {
        InvestSucceededEvent message = InvestSucceededEvent.of(projectId, investmentId, buyerAddress, tokenAmount, initialAmountPerToken);
        log.info("Sending InvestSucceededEvent to topic {}: {}", InvestSucceededEvent.TOPIC, message);
        sendMessage(InvestSucceededEvent.TOPIC, message);
    }

    public void sendInvestFailedEvent(String projectId, Long investmentId, String errorType, String errorMessage) {
        InvestFailedEvent message = InvestFailedEvent.of(projectId, investmentId, errorType, errorMessage);
        log.info("Sending InvestFailedEvent to topic {}: {}", InvestFailedEvent.TOPIC, message);
        sendMessage(InvestFailedEvent.TOPIC, message);
    }

    public void sendDepositSucceededEvent(String projectId, Long sellId, String sellerAddress, Long tokenAmount) {
        DepositSucceededEvent message = DepositSucceededEvent.of(projectId, sellId, sellerAddress, tokenAmount);
        log.info("Sending DepositSucceededEvent to topic {}: {}", DepositSucceededEvent.TOPIC, message);
        sendMessage(DepositSucceededEvent.TOPIC, message);
    }

    public void sendDepositFailedEvent(String projectId, Long sellId, String sellerAddress, Long tokenAmount, String errorMessage) {
        DepositFailedEvent message = DepositFailedEvent.of(projectId, sellId, sellerAddress, tokenAmount, errorMessage);
        log.info("Sending DepositFailedEvent to topic {}: {}", DepositFailedEvent.TOPIC, message);
        sendMessage(DepositFailedEvent.TOPIC, message);
    }

    public void sendDepositCancelSucceededEvent(String projectId, Long sellId, String sellerAddress, Long tokenAmount) {
        DepositCancelSucceededEvent message = DepositCancelSucceededEvent.of(projectId, sellId, sellerAddress, tokenAmount);
        log.info("Sending DepositCancelSucceededEvent to topic {}: {}", DepositCancelSucceededEvent.TOPIC, message);
        sendMessage(DepositCancelSucceededEvent.TOPIC, message);
    }

    public void sendDepositCancelFailedEvent(String projectId, Long sellId, String sellerAddress, Long tokenAmount, String errorMessage) {
        DepositCancelFailedEvent message = DepositCancelFailedEvent.of(projectId, sellId, sellerAddress, tokenAmount, errorMessage);
        log.info("Sending DepositCancelFailedEvent to topic {}: {}", DepositCancelFailedEvent.TOPIC, message);
        sendMessage(DepositCancelFailedEvent.TOPIC, message);
    }

    public void sendTradeRequestAcceptedEvent(String projectId, Long tradeId) {
        TradeRequestAcceptedEvent message = TradeRequestAcceptedEvent.of(projectId, tradeId);
        log.info("Sending TradeRequestAcceptedEvent to topic {}: {}", TradeRequestAcceptedEvent.TOPIC, message);
        sendMessage(TradeRequestAcceptedEvent.TOPIC, message);
    }

    public void sendTradeRequestRejectedEvent(String projectId, Long tradeId, String errorMessage) {
        TradeRequestRejectedEvent message = TradeRequestRejectedEvent.of(projectId, tradeId, errorMessage);
        log.info("Sending TradeRequestRejectedEvent to topic {}: {}", TradeRequestRejectedEvent.TOPIC, message);
        sendMessage(TradeRequestRejectedEvent.TOPIC, message);
    }

    public void sendTradeSucceededEvent(String projectId, Long tradeId, String buyerAddress, Long buyerTokenAmount, String sellerAddress, Long sellerTokenAmount) {
        TradeSucceededEvent message = TradeSucceededEvent.of(projectId, tradeId, buyerAddress, buyerTokenAmount, sellerAddress, sellerTokenAmount);
        log.info("Sending TradeSucceededEvent to topic {}: {}", TradeSucceededEvent.TOPIC, message);
        sendMessage(TradeSucceededEvent.TOPIC, message);
    }

    public void sendTradeFailedEvent(String projectId, Long tradeId, String errorType, String errorMessage) {
        TradeFailedEvent message = TradeFailedEvent.of(projectId, tradeId, errorType, errorMessage);
        log.info("Sending TradeFailedEvent to topic {}: {}", TradeFailedEvent.TOPIC, message);
        sendMessage(TradeFailedEvent.TOPIC, message);
    }
}
