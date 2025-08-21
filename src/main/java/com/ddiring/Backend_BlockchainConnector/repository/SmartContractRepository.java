package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmartContractRepository extends JpaRepository<SmartContract, Long> {
    List<SmartContract> findAllByIsActive(Boolean isActive);

    Optional<SmartContract> findBySmartContractAddress(String contractAddress);

    Boolean existsBySmartContractAddressOrProjectId(String contractAddress, String projectId);
}
