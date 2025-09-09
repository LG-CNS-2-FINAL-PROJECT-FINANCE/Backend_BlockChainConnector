package com.ddiring.Backend_BlockchainConnector.domain.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {
    @CreatedBy
    private String createdId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private String updatedId;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}