package com.ddiring.Backend_BlockchainConnector.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TerminationDto {
    @NotBlank
    private String projectId;
}
