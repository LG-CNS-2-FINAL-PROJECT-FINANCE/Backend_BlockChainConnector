package com.ddiring.Backend_BlockchainConnector.domain.entity;

import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventErrorType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventType;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "event_transaction_log")
@NoArgsConstructor
@AllArgsConstructor
public class EventTransactionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_contract_id", nullable = false)
    private SmartContract smartContractId;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "event_type", nullable = false)
    private OracleEventType oracleEventType;

    @Column(name = "transaction_result", nullable = false)
    private BlockchainRequestStatus blockchainRequestStatus;

    @Column(name = "error_type", nullable = true)
    private OracleEventErrorType errorType;

    @Column(name = "error_reason", nullable = true)
    private String errorReason;
}
