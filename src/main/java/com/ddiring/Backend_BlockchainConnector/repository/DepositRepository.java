package com.ddiring.Backend_BlockchainConnector.repository;

import com.ddiring.Backend_BlockchainConnector.domain.entity.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<Deposit,Long> {
    Boolean existsBySellIdAndDepositType(Long sellId, Deposit.DepositType depositType);
}
