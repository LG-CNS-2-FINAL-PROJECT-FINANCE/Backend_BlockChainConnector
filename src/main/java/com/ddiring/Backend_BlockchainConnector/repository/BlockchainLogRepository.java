package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlockchainLogRepository extends JpaRepository<BlockchainLog, Long> {

    Optional<BlockchainLog> findByProjectIdAndRequestStatus(String projectId, BlockchainRequestStatus requestStatus);

    Optional<BlockchainLog> findByProjectIdAndOrderIdAndRequestType(String projectId, Long orderId, BlockchainRequestType requestType);

    List<BlockchainLog> findByProjectIdAndOrderIdInAndRequestTypeAndRequestStatusIn(
            @NotBlank  String projectId,
            List<Long> orderIds,
            BlockchainRequestType requestType,
            List<BlockchainRequestStatus> requestStatuses
    );

    Boolean existsByProjectIdAndOrderIdAndRequestType(@NotBlank  String projectId, Long orderId, BlockchainRequestType requestType);

    Boolean existsByProjectIdAndRequestStatus(@NotBlank String projectId, BlockchainRequestStatus requestStatus);

    List<BlockchainLog> findByRequestTransactionHashInOrOracleTransactionHashIn(List<String> requestTransactionHashes, List<String> oracleTransactionHashes);
}
