package com.ddiring.Backend_BlockchainConnector.domain.entity;

import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Getter
@Builder
@Entity
@Table(name = "event_tracker", uniqueConstraints = @UniqueConstraint(columnNames = {"deployment_id", "oracle_event_type"}))
@NoArgsConstructor
@AllArgsConstructor
public class EventTracker extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployment_id", nullable = false)
    private Deployment deploymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oracle_event_type", nullable = false)
    private OracleEventType oracleEventType;

    @Column(name = "last_block_number", nullable = false)
    private BigInteger lastBlockNumber;

    public void updateBlockNumber(BigInteger blockNumber) {
        lastBlockNumber = blockNumber;
    }
}
