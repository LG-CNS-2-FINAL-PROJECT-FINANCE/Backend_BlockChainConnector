package com.ddiring.Backend_BlockchainConnector.event.producer;

import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeployFailedEvent;
import com.ddiring.Backend_BlockchainConnector.domain.event.deploy.DeploySucceededEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(String topic, Object message) {
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

}
