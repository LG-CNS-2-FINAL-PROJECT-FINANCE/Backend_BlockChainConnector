package com.ddiring.Backend_BlockchainConnector.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "smart_contract_events")
public class SmartContractEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id", nullable = false, unique = true)
    private Long eventId;

    @Column(name = "event_name", nullable = false, unique = true)
    private String eventName;
}
