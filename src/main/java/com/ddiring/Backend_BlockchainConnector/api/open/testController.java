package com.ddiring.Backend_BlockchainConnector.api.open;

import com.ddiring.Backend_BlockchainConnector.common.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(value = "/api/contract", produces = MediaType.APPLICATION_JSON_VALUE)
public class testController {
    @PostMapping("/investment_payment/verify")
    public ApiResponseDto<?> verifyInvestment(@RequestBody Map<String, List<Map<String, Object>>> investments) {
        List<Map<String, Object>> investRequestList = investments.get("investments");

        if (investRequestList == null || investRequestList.isEmpty()) {
            throw new IllegalArgumentException("투자 요청자가 없습니다.");
        }

        List<Boolean> resultList = new ArrayList<>(List.of());
        log.info("[INVEST] 체인링크 Functions로부터 결제 확인 요청이 들어왔습니다.");
        investRequestList.forEach(investment -> {
            log.info("Investment Id : {}, Investor Address : {}, Investment Token Amount : {}", investment.get("investmentId"), investment.get("investorAddress"), investment.get("tokenAmount"));
            resultList.add(true);
        });

        Map<String, List<Boolean>> result = Map.of("result", resultList);

        return ApiResponseDto.createOK(result);
    }

    @GetMapping("/trade_payment/verify")
    public ApiResponseDto<?> verifyTrade(@RequestParam("id") String requestId) {
        log.info("[TRADE] 체인링크 Functions로부터 결제 확인 요청이 들어왔습니다. 요청 ID: {}", requestId);

        Map<String, Boolean> result = Map.of("result", true);

        return ApiResponseDto.createOK(result);
    }
}
