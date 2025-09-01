package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SmartContractRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findAllByIsActive(Boolean isActive);

    Optional<Deployment> findBySmartContractAddress(String contractAddress);

    Boolean existsBySmartContractAddressOrProjectId(String contractAddress, String projectId);

    Optional<Deployment> findByProjectId(@NotBlank String projectId);
}
