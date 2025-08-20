package com.ddiring.Backend_BlockchainConnector.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "event_tracker", uniqueConstraints = @UniqueConstraint(columnNames = {"smart_contract_id", "event_id"}))
public class EventTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_contract_id", nullable = false)
    private SmartContract smartContractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private SmartContractEvent eventId;

    @Column(name = "last_block_number", nullable = false)
    private Long lastBlockNumber;
}
