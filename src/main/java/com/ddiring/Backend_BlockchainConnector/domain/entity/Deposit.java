package com.ddiring.Backend_BlockchainConnector.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@Table(name = "deposit")
@NoArgsConstructor
@AllArgsConstructor
public class Deposit {
    @Id
    @Column(name = "deposit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_contract_id", nullable = false)
    private SmartContract smartContract;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "sell_id", nullable = false)
    private Long sellId;

    @Column(name = "token_amount", nullable = false)
    private Long tokenAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "deposit_type", nullable = false)
    private DepositType depositType;

    public enum DepositType { DEPOSIT, CANCEL_DEPOSIT };

}
