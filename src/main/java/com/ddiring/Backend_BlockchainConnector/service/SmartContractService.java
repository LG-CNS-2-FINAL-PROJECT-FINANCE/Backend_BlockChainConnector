package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.config.JenkinsProperties;
import com.ddiring.Backend_BlockchainConnector.domain.dto.*;
import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;
import com.ddiring.Backend_BlockchainConnector.domain.mapper.BlockchainLogMapper;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.remote.deploy.RemoteJenkinsService;
import com.ddiring.Backend_BlockchainConnector.repository.BlockchainLogRepository;
import com.ddiring.Backend_BlockchainConnector.repository.DeploymentRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.transaction.Transactional;
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
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractService {
    private final ContractWrapper contractWrapper;
    private final KafkaMessageProducer kafkaMessageProducer;
    private final SmartContractEventManagementService eventManagementService;

    private final DeploymentRepository deploymentRepository;
    private final BlockchainLogRepository blockchainLogRepository;

    private final RemoteJenkinsService remoteJenkinsService;

    private final JenkinsProperties jenkinsProperties;

    @Transactional
    public void triggerDeploymentPipeline(DeployDto.Request deployRequestDto) {
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
                    deployRequestDto.getProjectId(),
                    deployRequestDto.getTokenName(),
                    deployRequestDto.getTokenSymbol(),
                    deployRequestDto.getTotalGoalAmount().toString(),
                    deployRequestDto.getMinAmount().toString()
            );

            BlockchainLog blockchainLog = BlockchainLogMapper.toEntityForDeploy(deployRequestDto.getProjectId());
            blockchainLogRepository.save(blockchainLog);

            log.info("Jenkins 배포 요청 성공: {}", jenkinsResponse.getStatusCode());
        } catch (RuntimeException e) {
            log.warn("[Deploy] Jenkins 배포 요청 실패: {}", e.getMessage());
            throw new RuntimeException("[Deploy] Jenkins 파이프라인 요청 중 오류 발생", e);
        }
    }

    @Transactional
    public void postDeployProcess(DeployDto.Response deployResponseDto) {
        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectId(deployResponseDto.getProjectId())
                .orElseThrow(() -> new NotFound("배포 요청한 기록이 없습니다."));

        if (!"success".equals(deployResponseDto.getStatus())) {
            log.error("스마트 컨트랙트 배포 실패: {}", deployResponseDto.getStatus());
            kafkaMessageProducer.sendDeployFailedEvent(deployResponseDto.getProjectId(), "스마트 컨트랙트 배포 실패");
            blockchainLog.updateDeployFailed();
            blockchainLogRepository.save(blockchainLog);
            return;
        }

        try {
            log.info("스마트 컨트랙트 배포 성공: {}", deployResponseDto.getAddress());

            EthGetTransactionReceipt ethGetTransactionReceipt = contractWrapper.getWeb3j().ethGetTransactionReceipt(deployResponseDto.getTransactionHash()).send();
            TransactionReceipt transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt()
                    .orElseThrow(() -> new NotFound("Can not get Transaction Hash"));

            Deployment contractInfo = eventManagementService.addSmartContract(
                deployResponseDto.getProjectId(),
                deployResponseDto.getAddress(),
                transactionReceipt.getBlockNumber()
            );

            blockchainLog.updateDeploySucceeded(contractInfo, transactionReceipt.getTransactionHash());
            blockchainLogRepository.save(blockchainLog);

            kafkaMessageProducer.sendDeploySucceededEvent(deployResponseDto.getProjectId());
        } catch (Exception e) {
            log.error("[DeployWebHook] 예상치 못한 에러 발생 : {}", e.getMessage());
            kafkaMessageProducer.sendDeployFailedEvent(deployResponseDto.getProjectId(), "예상치 못한 에러로 인한 배포 실패: " + e.getMessage());
        }
    }

    public void terminateSmartContract(TerminationDto terminationDto) {
        Deployment smartContract = deploymentRepository.findByProjectId(terminationDto.getProjectId())
                .orElseThrow(() -> new NotFound("찾을 수 없는 프로젝트입니다."));

        eventManagementService.removeSmartContract(smartContract);
    }

    @Transactional
    public void investment(InvestmentDto investmentDto) {
        try {
            if (investmentDto.getInvestInfoList().isEmpty()) {
                log.warn("투자 요청이 존재하지 않습니다.");
                throw new IllegalArgumentException("투자 요청이 존재하지 않습니다.");
            }

            Deployment contractInfo = deploymentRepository.findByProjectId(investmentDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

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

                        Stream<BlockchainLog> investLogList = investmentDto.getInvestInfoList().stream().map(investInfo -> {
                            return BlockchainLogMapper.toEntityForInvestment(contractInfo, response.getTransactionHash(), investInfo.getInvestmentId());
                        });
                        blockchainLogRepository.saveAll(investLogList.toList());

                    })
                    .exceptionally(throwable -> {
                        log.error("Investment request Error: {}", throwable.getMessage());
                        kafkaMessageProducer.sendInvestRequestRejectedEvent(investmentDto.getProjectId(), throwable.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            log.error("[Investment] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Investment] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public BalanceDto.Response getBalance(BalanceDto.Request balanceDto) {
        try {
            Deployment contractInfo = deploymentRepository.findByProjectId(balanceDto.getProjectId())
                    .orElseThrow(() -> new NotFound("스마트 컨트랙트를 찾을 수 없습니다"));

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            BigInteger tokenAmountWei = smartContract.balanceOf(balanceDto.getUserAddress()).send();
            BigInteger decimals = smartContract.decimals().send();
            Long tokenAmountDecimal = tokenAmountWei.divide(BigInteger.TEN.pow(decimals.intValue())).longValue();

            log.info("decimals: {}, tokenAmountWei: {}, tokenAmountDecimal: {}",
                    decimals, tokenAmountWei, tokenAmountDecimal);

            return BalanceDto.Response.builder().tokenAmount(tokenAmountDecimal).build();
        } catch (Exception e) {
            log.error("[Balance] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Balance] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }
}
