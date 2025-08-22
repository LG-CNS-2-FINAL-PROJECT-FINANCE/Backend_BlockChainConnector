package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTransactionLogRepository extends JpaRepository<EventTransactionLog, Long> {
    Boolean existsByTransactionHash(String transactionHash);
}
