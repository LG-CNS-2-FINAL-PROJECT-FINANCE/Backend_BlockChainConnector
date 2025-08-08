package com.ddiring.Backend_BlockchainConnector.api.open;

import com.ddiring.Backend_BlockchainConnector.common.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class testController {
    @GetMapping("/investment_payment/verify")
    public ApiResponseDto<?> verifyInvestment(@RequestParam("id") String requestId) {
        log.info("체인링크 Functions로부터 결제 확인 요청이 들어왔습니다. 요청 ID: {}", requestId);

        Map<String, Boolean> result = Map.of("result", true);

        return ApiResponseDto.createOK(result);
    }
}
