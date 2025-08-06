package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.dto.SmartContractDeployDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractService {
    private final RestTemplate restTemplate;

    @Value("${jenkins.url}")
    private String jenkinsUrl;
    @Value("${jenkins.username}")
    private String jenkinsUsername;
    @Value("${jenkins.api-token}")
    private String jenkinsApiToken;
    @Value("${jenkins.authentication-token}")
    private String jenkinsAuthenticationToken;
    @Value("${jenkins.job}")
    private String jenkinsJob;

    public void triggerDeploymentPipeline(SmartContractDeployDto deployDto) {
        String clumbKey;
        String clumbValue;
        String authHeader;

        try {
            String auth = jenkinsUsername + ":" + jenkinsApiToken;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.set("Authorization", authHeader);
            HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);

            String jenkinsClumbApiUrl = String.format("http://%s/crumbIssuer/api/json", jenkinsUrl);
            ResponseEntity<Map> clumbResponse = restTemplate.exchange(jenkinsClumbApiUrl, org.springframework.http.HttpMethod.GET, authEntity, Map.class);

            clumbKey = clumbResponse.getBody().get("crumbRequestField").toString();
            clumbValue = clumbResponse.getBody().get("crumb").toString();

            log.info("Jenkins Crumb 요청 성공: " + clumbResponse.getStatusCode());
        } catch (RuntimeException e) {
            log.warn("Jenkins Crumb 요청 실패: " + e.getMessage());
            throw new RuntimeException("Jenkins Crumb 요청 중 오류 발생", e);
        }

        try {
            String jenkinsApiUrl = String.format("http://%s%s/buildWithParameters", jenkinsUrl, jenkinsJob);

            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.add("Authorization", authHeader);
            postHeaders.add(clumbKey, clumbValue);
            postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("token", jenkinsAuthenticationToken);
            parameters.add("TOKEN_NAME", deployDto.getTokenName());
            parameters.add("TOKEN_SYMBOL", deployDto.getTokenSymbol());
            parameters.add("TOTAL_GOAL_AMOUNT", deployDto.getTotalGoalAmount().toString());
            parameters.add("MIN_AMOUNT", deployDto.getMinAmount().toString());

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, postHeaders);

            log.info("Jenkins Deploy Request Info : " + request.toString());

            ResponseEntity<String> jenkinsResponse = restTemplate.postForEntity(jenkinsApiUrl, request, String.class);
            log.info("Jenkins 배포 요청 성공: " + jenkinsResponse.getStatusCode());
        } catch (RuntimeException e) {
            log.warn("Jenkins 배포 요청 실패: " + e.getMessage());
            throw new RuntimeException("Jenkins 파이프라인 요청 중 오류 발생", e);
        }
    }
}
