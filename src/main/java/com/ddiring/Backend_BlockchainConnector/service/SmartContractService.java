package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.config.BlockchainProperties;
import com.ddiring.Backend_BlockchainConnector.config.JenkinsProperties;
import com.ddiring.Backend_BlockchainConnector.domain.dto.*;
import com.ddiring.Backend_BlockchainConnector.remote.deploy.RemoteJenkinsService;
import com.ddiring.Backend_BlockchainConnector.remote.deploy.RemoteProductService;
import com.ddiring.Backend_BlockchainConnector.remote.deploy.dto.UpdateContractAddressDto;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractService {
    private final ContractWrapper contractWrapper;

    private final RemoteJenkinsService remoteJenkinsService;
    private final RemoteProductService remoteProductService;

    private final JenkinsProperties jenkinsProperties;

    public void triggerDeploymentPipeline(SmartContractDeployDto deployDto) {
        String crumbValue;
        String authHeader;

        try {
            String auth = jenkinsProperties.getUsername() + ":" + jenkinsProperties.getApiToken();
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
            authHeader = "Basic " + new String(encodedAuth);

            Map<String, String> crumbResponse = remoteJenkinsService.getClumb(authHeader);
            crumbValue = crumbResponse.get("crumb");

            log.info("Jenkins Crumb 요청 성공");
        } catch (RuntimeException e) {
            log.warn("Jenkins Crumb 요청 실패: {}", e.getMessage());
            throw new RuntimeException("Jenkins Crumb 요청 중 오류 발생", e);
        }

        try {
            // OpenFeign 클라이언트의 메서드를 호출하여 Jenkins 빌드 요청
            ResponseEntity<Void> jenkinsResponse = remoteJenkinsService.requestSmartContractDeploy(
                    authHeader,
                    crumbValue,
                    jenkinsProperties.getAuthenticationToken(),
                    deployDto.getProjectId(),
                    deployDto.getTokenName(),
                    deployDto.getTokenSymbol(),
                    deployDto.getTotalGoalAmount().toString(),
                    deployDto.getMinAmount().toString()
            );

            log.info("Jenkins 배포 요청 성공: " + jenkinsResponse.getStatusCode());
        } catch (RuntimeException e) {
            log.warn("Jenkins 배포 요청 실패: " + e.getMessage());
            throw new RuntimeException("Jenkins 파이프라인 요청 중 오류 발생", e);
        }
    }

    public void postDeployProcess(SmartContractDeployResultDto resultDto) {
        try {
            if (!"success".equals(resultDto.getStatus())) {
                log.warn("토큰 등록 실패");
                throw new RuntimeException("토큰 등록 실패");
            }

            ResponseEntity<Void> response = remoteProductService.setContractAddress(
                    UpdateContractAddressDto.builder()
                            .projectId(resultDto.getProjectId())
                            .smartContractAddress(resultDto.getAddress())
                            .build()
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Product Service 스마트 컨트랙트 주소 업데이트 실패");

                throw new RuntimeException("Product Service 스마트 컨트랙트 주소 업데이트 실패");
            }
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public void investment(InvestmentDto investmentDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    investmentDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            smartContract.requestInvestment(
                    investmentDto.getInvestmentId().toString(),
                    investmentDto.getInvestorAddress(),
                    BigInteger.valueOf(investmentDto.getTokenAmount())
            ).sendAsync()
            .exceptionally(throwable -> {
                log.error("Investment request Error: {}", throwable.getMessage());
                throw new RuntimeException("Investment request Error: " + throwable.getMessage());
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void startTrade(TradeDto tradeDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    tradeDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            smartContract.approve(tradeDto.getSellerAddress(), BigInteger.valueOf(tradeDto.getTokenAmount()))
                    .sendAsync()
                    .thenAccept(result -> {
                        if (result.isStatusOK()) {
                            log.info("Approval successful: {}", result.getLogs());

                            requestTrade(tradeDto);
                        } else {
                            log.warn("Approval failed: {}", result.getLogs());
                        }
                    })
                    .exceptionally(throwable -> {
                        log.error("Approval Error: {}", throwable.getMessage());
                        throw new RuntimeException("Approval Error: " + throwable.getMessage());
                    });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void requestTrade(TradeDto tradeDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    tradeDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            smartContract.requestTrade(
                    tradeDto.getTradeId().toString(),
                    tradeDto.getSellerAddress(),
                    tradeDto.getBuyerAddress(),
                    BigInteger.valueOf(tradeDto.getTokenAmount())
            ).sendAsync()
            .exceptionally(throwable -> {
                log.error("Trade request Error: {}", throwable.getMessage());
                throw new RuntimeException("Trade request Error: " + throwable.getMessage());
            });

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public BalanceDto.Response getBalance(BalanceDto.Request balanceDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    balanceDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            BigInteger tokenAmountWei = smartContract.balanceOf(balanceDto.getUserAddress()).send();
            BigInteger divisor = new BigInteger("1000000000000000000"); // 10의 18제곱
            Long tokenAmountDecimal = tokenAmountWei.divide(divisor).longValue();

            return BalanceDto.Response.builder().tokenAmount(tokenAmountDecimal).build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
