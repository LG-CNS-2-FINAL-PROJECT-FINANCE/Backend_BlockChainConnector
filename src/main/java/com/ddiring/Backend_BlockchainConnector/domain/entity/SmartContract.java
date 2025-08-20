package com.ddiring.Backend_BlockchainConnector.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "smart_contracts")
public class SmartContract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "smart_contract_id", nullable = false, unique = true)
    private Long smartContractId;

    @Column(name = "project_id", nullable = false, unique = true)
    private Long projectId;

    @Column(name = "smart_contract_address", nullable = false, unique = true)
    private String smartContractAddress;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
