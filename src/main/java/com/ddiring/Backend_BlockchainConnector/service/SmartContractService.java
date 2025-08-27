package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.config.JenkinsProperties;
import com.ddiring.Backend_BlockchainConnector.domain.dto.*;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.remote.deploy.RemoteJenkinsService;
import com.ddiring.Backend_BlockchainConnector.repository.SmartContractRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractService {
    private final ContractWrapper contractWrapper;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final SmartContractEventManagementService eventManagementService;
    private final SmartContractRepository smartContractRepository;

    private final RemoteJenkinsService remoteJenkinsService;

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
        if (!"success".equals(resultDto.getStatus())) {
            log.error("스마트 컨트랙트 배포 실패: {}", resultDto.getStatus());
            kafkaMessageProducer.sendDeployFailedEvent(resultDto.getProjectId(), "스마트 컨트랙트 배포 실패");
            return;
        }

        try {
            log.info("스마트 컨트랙트 배포 성공: {}", resultDto.getAddress());

            EthGetTransactionReceipt ethGetTransactionReceipt = contractWrapper.getWeb3j().ethGetTransactionReceipt(resultDto.getTransactionHash()).send();
            TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt().orElseThrow();

            eventManagementService.addSmartContract(
                resultDto.getProjectId(),
                resultDto.getAddress(),
                transactionReceipt.getBlockNumber()
            );

            kafkaMessageProducer.sendDeploySucceededEvent(resultDto.getProjectId());
        } catch (Exception e) {
            log.error("예상치 못한 에러로 인한 배포 실패: {}", e.getMessage());
            kafkaMessageProducer.sendDeployFailedEvent(resultDto.getProjectId(), "예상치 못한 에러로 인한 배포 실패: " + e.getMessage());
        }
    }

    public void investment(InvestmentDto investmentDto) {
        try {
            if (investmentDto.getInvestInfoList().isEmpty()) {
                log.warn("투자 요청이 존재하지 않습니다.");
                throw new IllegalArgumentException("투자 요청이 존재하지 않습니다.");
            }

            SmartContract contractInfo = smartContractRepository.findByProjectId(investmentDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            // 동기 처리
            FractionalInvestmentToken smartContract;
            try {
                smartContract = FractionalInvestmentToken.load(
                        contractInfo.getSmartContractAddress(),
                        contractWrapper.getWeb3j(),
                        contractWrapper.getCredentials(),
                        contractWrapper.getGasProvider()
                );
            } catch (Exception e) {
                log.error("스마트 컨트랙트 로딩 에러: {}", e.getMessage());
                throw new RuntimeException("스마트 컨트랙트 로딩 에러: {}" + e.getMessage());
            }

            List<FractionalInvestmentToken.investment> investmentRequestInfoList = investmentDto.toSmartContractStruct();
            if (investmentRequestInfoList == null || investmentRequestInfoList.isEmpty()) {
                log.warn("변환된 투자 요청을 찾을 수 없습니다.");
                throw new IllegalArgumentException("변환된 투자 요청을 찾을 수 없습니다.");
            }

            // 비동기 처리
            smartContract.requestInvestment(investmentRequestInfoList).sendAsync()
                    .thenAccept(response -> {
                        log.info("Investment request accepted: {}", response.getLogs());
                        kafkaMessageProducer.sendInvestRequestAcceptedEvent(investmentDto.getProjectId());
                    })
                    .exceptionally(throwable -> {
                        log.error("Investment request Error: {}", throwable.getMessage());
                        kafkaMessageProducer.sendInvestRequestRejectedEvent(investmentDto.getProjectId(), throwable.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public BalanceDto.Response getBalance(BalanceDto.Request balanceDto) {
        try {
            SmartContract contractInfo = smartContractRepository.findByProjectId(balanceDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    contractInfo.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            BigInteger tokenAmountWei = smartContract.balanceOf(balanceDto.getUserAddress()).send();
            BigInteger decimals = smartContract.decimals().send();
            Long tokenAmountDecimal = tokenAmountWei.divide(BigInteger.TEN.pow(decimals.intValue())).longValue();

            log.info("decimals: {}, tokenAmountWei: {}, tokenAmountDecimal: {}",
                    decimals, tokenAmountWei, tokenAmountDecimal);

            return BalanceDto.Response.builder().tokenAmount(tokenAmountDecimal).build();
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 에러 발생 : " + e.getMessage());
        }
    }
}
