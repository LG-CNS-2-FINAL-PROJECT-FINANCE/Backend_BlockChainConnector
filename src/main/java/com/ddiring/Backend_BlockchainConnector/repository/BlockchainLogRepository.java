package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainLogRepository extends JpaRepository<BlockchainLog, Long> {
    Boolean existsByTransactionHash(String transactionHash);

    Optional<BlockchainLog> findByProjectId(String projectId);
}
