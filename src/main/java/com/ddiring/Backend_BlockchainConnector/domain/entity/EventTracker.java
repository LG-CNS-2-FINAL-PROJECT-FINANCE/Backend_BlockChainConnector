package com.ddiring.Backend_BlockchainConnector.domain.entity;

import com.ddiring.Backend_BlockchainConnector.domain.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@Builder
@Entity
@Table(name = "event_tracker", uniqueConstraints = @UniqueConstraint(columnNames = {"smart_contract_id", "event_type"}))
@NoArgsConstructor
@AllArgsConstructor
public class EventTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_contract_id", nullable = false)
    private SmartContract smartContractId;

    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(name = "last_block_number", nullable = false)
    private BigInteger lastBlockNumber;
}
