package com.ddiring.Backend_BlockchainConnector.event.producer;

import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeployFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeploySucceededEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.investment.InvestRequestAcceptedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.investment.InvestRequestRejectedEvent;
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

    public void sendInvestRequestRejectedEvent(Long investmentId, String reason) {
        InvestRequestRejectedEvent message = InvestRequestRejectedEvent.of(investmentId, reason);
        log.info("Sending InvestRequestRejectedEvent to topic {}: {}", InvestRequestRejectedEvent.TOPIC, message);
        sendMessage(InvestRequestRejectedEvent.TOPIC, message);
    }
}
