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
@Table(name = "blockchain_log")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private BlockchainRequestType requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false)
    private BlockchainRequestStatus requestStatus;

    @Column(name = "request_transaction_hash")
    private String requestTransactionHash;

    @Column(name = "order_id")
    private Long orderId; // Investment or Trade

    @Enumerated(EnumType.STRING)
    @Column(name = "oracle_event_type")
    private OracleEventType oracleEventType;

    @Column(name = "oracle_transaction_hash")
    private String oracleTransactionHash;

    @Enumerated(EnumType.STRING)
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

    public void updateSuccessResponse(String requestTransactionHash) {
        this.requestTransactionHash = requestTransactionHash;
        this.requestStatus = BlockchainRequestStatus.SUCCESS;
    }

    public void updateFailureResponse() {
        this.requestStatus = BlockchainRequestStatus.FAILURE;
    }

    public void updateOracleSuccessResponse(OracleEventType oracleEventType, String oracleTransactionHash) {
        this.oracleEventType = oracleEventType;
        this.oracleTransactionHash = oracleTransactionHash;
        this.requestStatus = BlockchainRequestStatus.SUCCESS;
    }

    public void updateOracleFailureResponse(OracleEventType oracleEventType, String oracleTransactionHash, OracleEventErrorType errorType, String errorReason) {
        this.oracleEventType = oracleEventType;
        this.oracleTransactionHash = oracleTransactionHash;
        this.requestStatus = BlockchainRequestStatus.FAILURE;
        this.errorType = errorType;
        this.errorReason = errorReason;
    }
}
