package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.common.exception.NotFound;
import com.ddiring.Backend_BlockchainConnector.config.BlockchainProperties;
import com.ddiring.Backend_BlockchainConnector.config.JenkinsProperties;
import com.ddiring.Backend_BlockchainConnector.domain.dto.*;
import com.ddiring.Backend_BlockchainConnector.domain.entity.BlockchainLog;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestStatus;
import com.ddiring.Backend_BlockchainConnector.domain.enums.BlockchainRequestType;
import com.ddiring.Backend_BlockchainConnector.domain.mapper.BlockchainLogMapper;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.remote.deploy.RemoteJenkinsService;
import com.ddiring.Backend_BlockchainConnector.remote.log.RemoteEtherscanService;
import com.ddiring.Backend_BlockchainConnector.repository.BlockchainLogRepository;
import com.ddiring.Backend_BlockchainConnector.repository.DeploymentRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
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
    private final RemoteEtherscanService remoteEtherscanService;

    private final JenkinsProperties jenkinsProperties;
    private final BlockchainProperties blockchainProperties;

    @Transactional
    public void triggerDeploymentPipeline(DeployDto.Request deployRequestDto) {
        log.info("[스마트 컨트랙트 배포 요청]");

        if (deploymentRepository.existsByProjectId(deployRequestDto.getProjectId())) {
            throw new EntityExistsException("이미 배포된 스마트 컨트랙트입니다.");
        }

        if (blockchainLogRepository.existsByProjectIdAndRequestStatus(deployRequestDto.getProjectId(), BlockchainRequestStatus.PENDING)) {
            throw new EntityExistsException("배포 요청이 진행 중입니다.");
        }

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
        log.info("[스마트 컨트랙트 배포 결과 응답]");

        if (deploymentRepository.existsByProjectId(deployResponseDto.getProjectId())) {
            throw new EntityExistsException("이미 배포된 스마트 컨트랙트입니다.");
        }

        BlockchainLog blockchainLog = blockchainLogRepository.findByProjectIdAndRequestStatus(deployResponseDto.getProjectId(), BlockchainRequestStatus.PENDING)
                .orElseGet(() -> {
                    log.error("배포 요청한 기록이 없습니다. 입력받은 프로젝트 번호 : {}", deployResponseDto.getAddress());
                    throw new NotFound("배포 요청한 기록이 없습니다.");
                });

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
                    .orElseGet(() -> {
                        log.error("트랜잭션 해시를 가져 올 수 없습니다.");
                        throw new NotFound("트랜잭션 해시를 가져 올 수 없습니다.");
                    });

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
        log.info("[스마트 컨트랙트 비활성화]");

        Deployment smartContract = deploymentRepository.findByProjectId(terminationDto.getProjectId())
                .orElseGet(() -> {
                    log.error("찾을 수 없는 프로젝트 번호: {} 입니다.", terminationDto.getProjectId());
                    throw new NotFound("찾을 수 없는 프로젝트입니다.");
                });

        eventManagementService.removeSmartContract(smartContract);
    }

    private InvestmentDto filterInvestRequest(InvestmentDto investmentDto) {
        List<Long> allInvestmentIds = investmentDto.getInvestInfoList().stream()
                .map(InvestmentDto.InvestInfo::getInvestmentId)
                .toList();

        List<BlockchainRequestStatus> targetStatuses = List.of(
                BlockchainRequestStatus.PENDING,
                BlockchainRequestStatus.SUCCESS
        );

        List<BlockchainLog> existingLogs = blockchainLogRepository
                .findByProjectIdAndOrderIdInAndRequestTypeAndRequestStatusIn(
                        investmentDto.getProjectId(),
                        allInvestmentIds,
                        BlockchainRequestType.INVESTMENT,
                        targetStatuses
                );

        Set<Long> processedInvestmentIds = existingLogs.stream()
                .map(BlockchainLog::getOrderId)
                .collect(Collectors.toSet());

        List<InvestmentDto.InvestInfo> filteredInvestInfoList = investmentDto.getInvestInfoList().stream()
                .filter(investInfo -> !processedInvestmentIds.contains(investInfo.getInvestmentId()))
                .toList();

        if (filteredInvestInfoList.isEmpty()) {
            log.warn("요청된 모든 투자 ID가 이미 처리 중이거나 성공한 상태입니다.");
            throw new IllegalArgumentException("요청된 모든 투자 ID가 이미 처리 중이거나 성공한 상태입니다.");
        }

        return InvestmentDto.builder()
                .projectId(investmentDto.getProjectId())
                .investInfoList(filteredInvestInfoList)
                .build();
    }

    @Transactional
    public void investment(InvestmentDto investmentDto) {
        log.info("[스마트 컨트랙트 투자 요청 처리]");

        try {
            if (investmentDto.getInvestInfoList() == null || investmentDto.getInvestInfoList().isEmpty()) {
                log.warn("투자 요청이 존재하지 않습니다.");
                throw new IllegalArgumentException("투자 요청이 존재하지 않습니다.");
            }

            Deployment contractInfo = deploymentRepository.findByProjectId(investmentDto.getProjectId())
                    .orElseGet(() -> {
                        log.error("{}에 해당하는 스마트 컨트랙트를 찾을 수 없습니다.", investmentDto.getProjectId());
                        throw new NotFound("스마트 컨트랙트를 찾을 수 없습니다.");
                    });

            FractionalInvestmentToken smartContract = contractWrapper.getSmartContract(contractInfo.getSmartContractAddress());

            InvestmentDto filteredInvestmentDto = filterInvestRequest(investmentDto);
            List<FractionalInvestmentToken.investment> investmentRequestInfoList = filteredInvestmentDto.toSmartContractStruct();
            if (investmentRequestInfoList == null || investmentRequestInfoList.isEmpty()) {
                log.warn("변환된 투자 요청을 찾을 수 없습니다.");
                throw new IllegalArgumentException("변환된 투자 요청을 찾을 수 없습니다.");
            }

            // 비동기 처리
            smartContract.requestInvestment(investmentRequestInfoList).sendAsync()
                    .thenAccept(response -> {
                        log.info("Investment request accepted: {}", response.getLogs());
                        filteredInvestmentDto.getInvestInfoList().forEach(investmentInfo -> {
                            kafkaMessageProducer.sendInvestRequestAcceptedEvent(
                                    investmentDto.getProjectId(),
                                    investmentInfo.getInvestmentId(),
                                    investmentInfo.getInvestorAddress(),
                                    investmentInfo.getTokenAmount()
                            );
                        });

                        Stream<BlockchainLog> investLogList = investmentDto.getInvestInfoList().stream().map(investInfo -> {
                            return BlockchainLogMapper.toEntityForInvestment(contractInfo, response.getTransactionHash(), investInfo.getInvestmentId());
                        });
                        blockchainLogRepository.saveAll(investLogList.toList());

                    })
                    .exceptionally(throwable -> {
                        log.error("Investment request Error: {}", throwable.getMessage());
                        filteredInvestmentDto.getInvestInfoList().forEach(investmentInfo -> {
                            kafkaMessageProducer.sendInvestRequestRejectedEvent(
                                    investmentDto.getProjectId(),
                                    investmentInfo.getInvestmentId(),
                                    investmentInfo.getInvestorAddress(),
                                    investmentInfo.getTokenAmount(),
                                    throwable.getMessage()
                            );
                        });
                        return null;
                    });
        } catch (Exception e) {
            log.error("[Investment] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[Investment] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }

    public BalanceDto.Response getBalance(BalanceDto.Request balanceDto) {
        log.info("[사용자 잔액 조회]");

        try {
            Deployment contractInfo = deploymentRepository.findByProjectId(balanceDto.getProjectId())
                    .orElseGet(() -> {
                        log.error("{}에 해당하는 스마트 컨트랙트를 찾을 수 없습니다.", balanceDto.getProjectId());
                        throw new NotFound("스마트 컨트랙트를 찾을 수 없습니다.");
                    });

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

    public LogsDto.Response getLogs(LogsDto.Request logsDto) {
        log.info("[스마트 컨트랙트 로그 조회]");

        Deployment deploymennt = deploymentRepository.findByProjectId(logsDto.getProjectId())
                        .orElseGet(() -> {
                            log.error("{}에 해당하는 스마트 컨트랙트를 찾을 수 없습니다.", logsDto.getProjectId());
                            throw new NotFound("스마트 컨트랙트를 찾을 수 없습니다.");
                        });

        try {
            Long chainId = contractWrapper.getWeb3j().ethChainId().send().getChainId().longValue();

            EtherscanEventLogDto.Request etherscanRequst = EtherscanEventLogDto.of(
                    logsDto, chainId,
                    deploymennt.getSmartContractAddress(),
                    blockchainProperties.getEtherscan().getApi().getKey()
            );
            EtherscanEventLogDto.Response etherScanResponse = remoteEtherscanService.getLogsFromEtherScan(
                    etherscanRequst.getChainId(),
                    etherscanRequst.getModule(),
                    etherscanRequst.getAction(),
                    etherscanRequst.getContractaddress(),
                    etherscanRequst.getAddress(),
                    etherscanRequst.getPage(),
                    etherscanRequst.getOffset(),
                    etherscanRequst.getStartblock(),
                    etherscanRequst.getEndblock(),
                    etherscanRequst.getSort(),
                    etherscanRequst.getApiKey()
            );

            log.info("Response Status: {}, Message: {}, Result Size: {}", etherScanResponse.getStatus(), etherScanResponse.getMessage(), etherScanResponse.getResult().size());

            // 트랜잭션 해시 - Request Type 매핑
            List<String> filterHashList = etherScanResponse.getResult().stream().map(EtherscanEventLogDto.Response.TokenTransaction::getHash).toList();
            List<BlockchainLog> blockchainLogList = blockchainLogRepository.findByRequestTransactionHashInOrOracleTransactionHashIn(filterHashList, filterHashList);
            Map<String, BlockchainRequestType> transactionTypeMap = blockchainLogList.stream().collect(Collectors.toMap(
                    log -> {
                        String oracleTransactionHash = log.getOracleTransactionHash();
                        if (oracleTransactionHash == null || oracleTransactionHash.isBlank()) {
                            return log.getRequestTransactionHash();
                        } else {
                            return oracleTransactionHash;
                        }
                    },
                    BlockchainLog::getRequestType,
                    (existingValue, newValue) -> existingValue
            ));

            // 응답 DTO 구성
            Stream<LogsDto.Response.TransactionLog> response = etherScanResponse.getResult().stream().map(transactionLog -> {
                String transactionType = transactionTypeMap.getOrDefault(transactionLog.getHash(), BlockchainRequestType.ETC).toString();

                return EtherscanEventLogDto.toLogsResponse(transactionLog, transactionType);
            });

            return LogsDto.Response.builder().result(response.toList()).build();
        } catch (Exception e) {
            log.error("[EtherscanLog] 예상치 못한 에러 발생 : {}", e.getMessage());
            throw new RuntimeException("[EtherscanLog] 예상치 못한 에러 발생 : " + e.getMessage());
        }
    }
}
