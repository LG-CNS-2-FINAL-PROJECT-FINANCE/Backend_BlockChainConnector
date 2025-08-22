package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTrackerRepository extends JpaRepository<EventTracker,Long> {
    List<EventTracker> findAllBySmartContractId_SmartContractId(Long smartContractId);

    Optional<EventTracker> findByEventTypeAndSmartContractId_SmartContractAddress(EventType eventType, String smartContractAddress);
}
