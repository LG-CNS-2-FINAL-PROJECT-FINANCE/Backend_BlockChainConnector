package com.ddiring.Backend_BlockchainConnector.api.open;

import com.ddiring.Backend_BlockchainConnector.common.dto.ApiResponseDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.SmartContractDeployDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.SmartContractDeployResultDto;
import com.ddiring.Backend_BlockchainConnector.service.SmartContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/contract", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SmartContractController {
    private final SmartContractService smartContractService;

    @PostMapping("/deploy")
    public ApiResponseDto<?> deployContract(@RequestBody @Valid SmartContractDeployDto deployDto) {
        smartContractService.triggerDeploymentPipeline(deployDto);
        return ApiResponseDto.defaultOK();
    }

    @PostMapping("/deploy/result")
    public ApiResponseDto<?> receiveDeployResult(@RequestBody @Valid SmartContractDeployResultDto resultDto) {
        // TODO: Chainlink Functions Consumer 등록 API(?) 추가

        // TODO: 상품 서비스에 컨트랙트 주소 전송 기능 추가

        log.info(String.format("""
            Contract Address : %s,
            Token Name : %s,
            Token Symbol %s
        """, resultDto.getAddress(), resultDto.getName(), resultDto.getSymbol()));

        return ApiResponseDto.defaultOK();
    }
}
