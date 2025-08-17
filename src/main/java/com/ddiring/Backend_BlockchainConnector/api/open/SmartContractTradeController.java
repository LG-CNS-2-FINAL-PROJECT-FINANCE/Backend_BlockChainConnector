package com.ddiring.Backend_BlockchainConnector.api.open;

import com.ddiring.Backend_BlockchainConnector.common.dto.ApiResponseDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.DepositWithPermitDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.TradeDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.signature.PermitSignatureDto;
import com.ddiring.Backend_BlockchainConnector.service.SmartContractTradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/contract/trade", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SmartContractTradeController {
    private final SmartContractTradeService smartContractTradeService;

    @PostMapping(value = "/signature")
    public ApiResponseDto<?> getSignature(@RequestBody @Valid PermitSignatureDto.Request permitSignatureDto) {
        PermitSignatureDto.Response response = smartContractTradeService.getSignature(permitSignatureDto);

        return ApiResponseDto.createOK(response);
    }

    @PostMapping(value = "/deposit")
    public ApiResponseDto<?> depositTokenWithPermit(@RequestBody @Valid DepositWithPermitDto depositDto) {
        smartContractTradeService.deposit(depositDto);

        return ApiResponseDto.defaultOK();
    }

    @PostMapping(value = "/execute")
    public ApiResponseDto<?> tradeToken(@RequestBody @Valid TradeDto tradeDto) {
        smartContractTradeService.trade(tradeDto);

        return ApiResponseDto.defaultOK();
    }
}
