package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTrackerRepository extends JpaRepository<EventTracker,Long> {
    List<EventTracker> findAllByDeploymentId_SmartContractId(Long smartContractId);

    Optional<EventTracker> findByOracleEventTypeAndDeploymentId_SmartContractAddress(OracleEventType oracleEventType, String smartContractAddress);
}
