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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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
    @Value("${jenkins.job-name}")
    private String jenkinsJobName;

    public void triggerDeploymentPipeline(SmartContractDeployDto deployDto) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("TOKEN_NAME", deployDto.getTokenName());
        parameters.add("TOKEN_SYMBOL", deployDto.getTokenSymbol());
        parameters.add("TOTAL_GOAL_AMOUNT", deployDto.getTotalGoalAmount().toString());
        parameters.add("MIN_AMOUNT", deployDto.getMinAmount().toString());

        String jenkinsApiUrl = String.format("%s/job/%s/buildWithParameters", jenkinsUrl, jenkinsJobName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(jenkinsApiUrl, request, String.class);
            log.info("Jenkins 배포 요청 성공: " + response.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.warn("Jenkins 배포 요청 실패: " + e.getResponseBodyAsString());
            throw new RuntimeException("Jenkins 파이프라인 요청 중 오류 발생", e);
        }
    }
}
