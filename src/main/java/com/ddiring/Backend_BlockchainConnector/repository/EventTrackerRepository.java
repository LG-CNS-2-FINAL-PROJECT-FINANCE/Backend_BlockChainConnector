package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventTrackerRepository extends JpaRepository<EventTracker,Long> {
    List<EventTracker> findAllBySmartContractId_SmartContractId(Long smartContractId);

    EventTracker findBySmartContractId_SmartContractAddress(String smartContractAddress);
}
