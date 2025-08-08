package com.ddiring.Backend_BlockchainConnector.remote.deploy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "RemoteJenkinsService", url = "${jenkins.url}")
public interface RemoteJenkinsService {

    @GetMapping(value = "/crumbIssuer/api/json")
    Map<String, String> getClumb(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader);

    @PostMapping(value = "/${jenkins.job}/buildWithParameters")
    ResponseEntity<Void> requestSmartContractDeploy(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestHeader("Jenkins-Crumb") String crumbValue,
            @RequestParam("token") String token, // Jenkins API Token
            @RequestParam("PROJECT_ID") String projectId,
            @RequestParam("TOKEN_NAME") String tokenName,
            @RequestParam("TOKEN_SYMBOL") String tokenSymbol,
            @RequestParam("TOTAL_GOAL_AMOUNT") String totalGoalAmount,
            @RequestParam("MIN_AMOUNT") String minAmount
    );
}
