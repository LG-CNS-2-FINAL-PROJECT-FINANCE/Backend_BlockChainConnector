package com.ddiring.Backend_BlockchainConnector.domain.entity;

import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;
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
public class BlockchainLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_contract_id", nullable = false)
    private SmartContract smartContractId;

    @Column(name = "request_type", nullable = false)
    private BlockchainRequestType requestType;

    @Column(name = "request_status", nullable = false)
    private BlockchainRequestStatus requestStatus;

    @Column(name = "transaction_hash")
    private String transactionHash;

    @Column(name = "oracle_event_type")
    private OracleEventType oracleEventType;

    @Column(name = "error_type")
    private OracleEventErrorType errorType;

    @Column(name = "error_reason")
    private String errorReason;
}
