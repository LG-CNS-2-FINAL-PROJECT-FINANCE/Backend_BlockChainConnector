package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.dto.BalanceDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.InvestmentDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.SmartContractDeployDto;
import com.ddiring.Backend_BlockchainConnector.domain.dto.SolidityFunctionWrapperDto;
import com.ddiring.contract.FractionalInvestmentToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DynamicGasProvider;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractService {
    private final RestTemplate restTemplate;

    private final JenkinsProperties jenkinsProperties;
    private final BlockchainProperties blockchainProperties;

    public void triggerDeploymentPipeline(SmartContractDeployDto deployDto) {
        String clumbKey;
        String clumbValue;
        String authHeader;

        try {
            String auth = jenkinsProperties.getUsername() + ":" + jenkinsProperties.getApiToken();
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            authHeader = "Basic " + new String(encodedAuth);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.set("Authorization", authHeader);
            HttpEntity<String> authEntity = new HttpEntity<>(authHeaders);

            String jenkinsClumbApiUrl = String.format("http://%s/crumbIssuer/api/json", jenkinsProperties.getUrl());
            ResponseEntity<Map> clumbResponse = restTemplate.exchange(jenkinsClumbApiUrl, org.springframework.http.HttpMethod.GET, authEntity, Map.class);

            clumbKey = clumbResponse.getBody().get("crumbRequestField").toString();
            clumbValue = clumbResponse.getBody().get("crumb").toString();

            log.info("Jenkins Crumb 요청 성공: " + clumbResponse.getStatusCode());
        } catch (RuntimeException e) {
            log.warn("Jenkins Crumb 요청 실패: " + e.getMessage());
            throw new RuntimeException("Jenkins Crumb 요청 중 오류 발생", e);
        }

        try {
            String jenkinsApiUrl = String.format("http://%s%s/buildWithParameters", jenkinsProperties.getUrl(), jenkinsProperties.getJob());

            HttpHeaders postHeaders = new HttpHeaders();
            postHeaders.add("Authorization", authHeader);
            postHeaders.add(clumbKey, clumbValue);
            postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("token", jenkinsProperties.getAuthenticationToken());
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

    private SolidityFunctionWrapperDto setupSoldityFunctionWrapper() {
        try {
            Web3j web3j = Web3j.build(new HttpService(blockchainProperties.getSepolia().getRpc().getUrl()));
            Credentials credentials = Credentials.create(blockchainProperties.getAdmin().getKey().getPrivateKey());
            DynamicGasProvider gasProvider = new DynamicGasProvider(web3j);
            return SolidityFunctionWrapperDto.builder()
                    .web3j(web3j)
                    .credentials(credentials)
                    .gasProvider(gasProvider)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Web3j 설정 실패 : " + e);
        }
    }

    public void investment(InvestmentDto investmentDto) {
        try {
            SolidityFunctionWrapperDto solidityFunctionWrapperDto = setupSoldityFunctionWrapper();

            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(investmentDto.getSmartContractAddress(), solidityFunctionWrapperDto.getWeb3j(), solidityFunctionWrapperDto.getCredentials(), solidityFunctionWrapperDto.getGasProvider());

            smartContract.requestInvestment(investmentDto.getInvestmentId().toString(), investmentDto.getInvestorAddress(), BigInteger.valueOf(investmentDto.getTokenAmount())).sendAsync();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public BalanceDto.Response getBalance(BalanceDto.Request balanceDto) {
        try {
            SolidityFunctionWrapperDto solidityFunctionWrapperDto = setupSoldityFunctionWrapper();

            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(balanceDto.getSmartContractAddress(), solidityFunctionWrapperDto.getWeb3j(), solidityFunctionWrapperDto.getCredentials(), solidityFunctionWrapperDto.getGasProvider());

            BigInteger tokenAmountWei = smartContract.balanceOf(balanceDto.getUserAddress()).send();
            BigInteger divisor = new BigInteger("1000000000000000000"); // 10의 18제곱
            Long tokenAmountDecimal = tokenAmountWei.divide(divisor).longValue();

            return BalanceDto.Response.builder().tokenAmount(tokenAmountDecimal).build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
