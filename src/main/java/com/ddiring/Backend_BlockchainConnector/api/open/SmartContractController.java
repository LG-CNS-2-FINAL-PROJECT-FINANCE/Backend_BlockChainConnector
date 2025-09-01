package com.ddiring.Backend_BlockchainConnector.api.open;

import com.ddiring.Backend_BlockchainConnector.common.dto.ApiResponseDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.*;
import com.ddiring.Backend_BlockchainConnector.service.SmartContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/contract", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SmartContractController {
    private final SmartContractService smartContractService;

    @PostMapping("/deploy")
    public ApiResponseDto<?> deployContract(@RequestBody @Valid DeployDto.Request deployRequestDto) {
        smartContractService.triggerDeploymentPipeline(deployRequestDto);
        return ApiResponseDto.defaultOK();
    }

    @PostMapping("/deploy/result")
    public ApiResponseDto<?> receiveDeployResult(@RequestBody @Valid DeployDto.Response deployResponseDto) {
        smartContractService.postDeployProcess(deployResponseDto);

        return ApiResponseDto.defaultOK();
    }

    @PostMapping(value = "/investment")
    public ApiResponseDto<?> transferToken(@RequestBody @Valid InvestmentDto investmentDto) {
        smartContractService.investment(investmentDto);

        return ApiResponseDto.createOK("투자 요청이 완료되었습니다.");
    }

    @GetMapping(value = "/balance")
    public ApiResponseDto<?> getBalance(@ModelAttribute @Valid BalanceDto.Request balanceDto) {
        BalanceDto.Response response = smartContractService.getBalance(balanceDto);

        return ApiResponseDto.createOK(response);
    }
}
