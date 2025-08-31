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
    @JoinColumn(name = "smart_contract_id")
    private SmartContract smartContract;

    @Column(name = "project_id", nullable = false)
    private String projectId;

    @Column(name = "request_type", nullable = false)
    private BlockchainRequestType requestType;

    @Column(name = "request_status", nullable = false)
    private BlockchainRequestStatus requestStatus;

    @Column(name = "request_transaction_hash")
    private String requestTransactionHash;

    @Column(name = "order_id", columnDefinition = "Investment or Trade")
    private Long orderId;

    @Column(name = "oracle_event_type")
    private OracleEventType oracleEventType;

    @Column(name = "oracle_transaction_hash")
    private String oracleTransactionHash;

    @Column(name = "error_type")
    private OracleEventErrorType errorType;

    @Column(name = "error_reason")
    private String errorReason;

    public void updateDeploySucceeded(SmartContract contract, String requestTransactionHash) {
        if (this.smartContract != null) {
            throw new EntityExistsException("이미 배포된 스마트 컨트랙트입니다.");
        }

        this.smartContract = contract;
        this.requestTransactionHash = requestTransactionHash;
        this.requestStatus = BlockchainRequestStatus.SUCCESS;
    }

    public void updateDeployFailed() {
        this.requestStatus = BlockchainRequestStatus.FAILURE;
    }

    }
}
