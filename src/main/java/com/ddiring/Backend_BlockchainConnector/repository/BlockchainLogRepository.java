package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockchainLogRepository extends JpaRepository<BlockchainLog, Long> {
    Boolean existsByTransactionHash(String transactionHash);
}
