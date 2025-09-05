package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.entity.Deployment;
import com.ddiring.Backend_BlockchainConnector.domain.enums.OracleEventType;
import com.ddiring.Backend_BlockchainConnector.domain.records.EventFunctionMapping;
import com.ddiring.Backend_BlockchainConnector.event.producer.KafkaMessageProducer;
import com.ddiring.Backend_BlockchainConnector.repository.EventTrackerRepository;
import com.ddiring.Backend_BlockchainConnector.repository.DeploymentRepository;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.BaseEventResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractEventManagementService {
    private final SmartContractEventProcessorService eventProcessorService;

    private final Map<String, List<Disposable>> activeDisposables = new ConcurrentHashMap<>();
    private final Map<OracleEventType, EventFunctionMapping> eventFunctionMap = new EnumMap<>(OracleEventType.class);
    private final Map<String, Integer> reconnectAttempts = new ConcurrentHashMap<>();

    private final DeploymentRepository deploymentRepository;
    private final EventTrackerRepository eventTrackerRepository;

    private final ContractWrapper contractWrapper;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final int BASE_DELAY = 5;    // 초 단위
    private static final int MAX_DELAY = 300;   // 초 단위 (5분)
    private static final int MAX_RETRIES = 10;    // 최대 재시도 횟수
    private final KafkaMessageProducer kafkaMessageProducer;


    @PostConstruct
    public void init() {
        initEventFunctionMap();

        List<Deployment> deploymentList = deploymentRepository.findAllByIsActive(true);
        for (Deployment contract : deploymentList) {
            setupAllEventFilter(contract);
        }
    }

    private void initEventFunctionMap() {
        eventFunctionMap.put(
                OracleEventType.INVESTMENT_SUCCESSFUL,
                new EventFunctionMapping(
                        "investmentSuccessfulEventFlowable",
                        event -> eventProcessorService.handleInvestmentSuccessful((FractionalInvestmentToken.InvestmentSuccessfulEventResponse) event)
                )
        );
        eventFunctionMap.put(
                OracleEventType.INVESTMENT_FAILED,
                new EventFunctionMapping(
                        "investmentFailedEventFlowable",
                        event -> eventProcessorService.handleInvestmentFailed((FractionalInvestmentToken.InvestmentFailedEventResponse) event)
                )
        );
        eventFunctionMap.put(
                OracleEventType.TRADE_SUCCESSFUL,
                new EventFunctionMapping(
                        "tradeSuccessfulEventFlowable",
                        event -> eventProcessorService.handleTradeSuccessful((FractionalInvestmentToken.TradeSuccessfulEventResponse) event)
                )
        );
        eventFunctionMap.put(
                OracleEventType.TRADE_FAILED,
                new EventFunctionMapping(
                        "tradeFailedEventFlowable",
                        event -> eventProcessorService.handleTradeFailed((FractionalInvestmentToken.TradeFailedEventResponse) event)
                )
        );
    }

    @Transactional
    public Deployment addSmartContract(String projectId, String smartContractAddress, BigInteger blockNumber) {
        if (projectId == null) {
            log.error("프로젝트 ID가 필요합니다.");
            throw new IllegalArgumentException("프로젝트 ID가 필요합니다.");
        }

        if (smartContractAddress == null || smartContractAddress.isBlank()) {
            log.error("유효하지 않은 계약 정보입니다: {}", smartContractAddress);
            throw new IllegalArgumentException("유효하지 않은 계약 정보입니다.");
        }

        if (blockNumber == null || blockNumber.compareTo(BigInteger.ZERO) < 0) {
            log.error("유효하지 않은 블록 번호입니다: {}", blockNumber);
            throw new IllegalArgumentException("유효하지 않은 블록 번호입니다.");
        }

        if (deploymentRepository.existsBySmartContractAddressOrProjectId(smartContractAddress, projectId)) {
            log.error("계약 주소 또는 프로젝트 ID가 이미 존재합니다. Address: {}, Project ID: {}", smartContractAddress, projectId);
            throw new IllegalArgumentException("이미 존재하는 계약입니다.");
        }

        if (activeDisposables.containsKey(smartContractAddress)) {
            log.error("이미 이벤트 필터가 등록되어 있습니다: {}", smartContractAddress);
            throw new IllegalArgumentException("이미 이벤트 필터가 등록되어 있습니다: " + smartContractAddress);
        }

        try {
            // 계약 활성화
            log.info("Activating new smart contract: {}", smartContractAddress);
            Deployment contract = Deployment.builder()
                    .projectId(projectId)
                    .smartContractAddress(smartContractAddress)
                    .isActive(true)
                    .build();
            deploymentRepository.save(contract);

            // 이벤트 필터 설정
            log.info("Adding new smart contract: {}", smartContractAddress);
            List<EventTracker> eventTrackers = new ArrayList<>();
            OracleEventType.getAllEvent().forEach(event -> {
                EventTracker eventTracker = EventTracker.builder()
                        .deploymentId(contract)
                        .oracleEventType(event)
                        .lastBlockNumber(blockNumber)
                        .build();
                eventTrackers.add(eventTracker);
                log.info("Adding event tracker for contract: {}, event: {}", smartContractAddress, event.getEventName());
            });
            eventTrackerRepository.saveAll(eventTrackers);

            setupAllEventFilter(contract);

            return contract;
        } catch (Exception e) {
            log.error("[스마트 컨트랙트 등록 실패] {}", e.getMessage());

            removeAllEventFilter(smartContractAddress);

            throw new RuntimeException("[스마트 컨트랙트 등록 실패] " + e.getMessage());
        }
    }

    @Transactional
    public void removeSmartContract(Deployment contract) {
        if (contract == null || contract.getSmartContractAddress() == null) {
            throw new IllegalArgumentException("유효하지 않은 계약 정보입니다.");
        }

        if (contract.getIsActive() == null || !contract.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계약입니다.");
        }

        if (!activeDisposables.containsKey(contract.getSmartContractAddress())) {
            throw new IllegalArgumentException("등록된 이벤트 필터가 없습니다: " + contract.getSmartContractAddress());
        }

        if (!deploymentRepository.existsById(contract.getSmartContractId())) {
            throw new IllegalArgumentException("해당 계약이 존재하지 않습니다: " + contract.getSmartContractAddress());
        }

        // 계약 비활성화
        log.info("Deactivating contract: {}", contract.getSmartContractAddress());
        contract.deactivate();
        deploymentRepository.save(contract);

        removeAllEventFilter(contract.getSmartContractAddress());
    }

    private void setupAllEventFilter(Deployment deployment) {
        if (deployment == null || deployment.getSmartContractAddress() == null) {
            throw new IllegalArgumentException("유효하지 않은 계약 정보입니다.");
        }

        if (deployment.getIsActive() == null || !deployment.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계약입니다.");
        }

        // 기존 구독 해제
        removeAllEventFilter(deployment.getSmartContractAddress());

        List<EventTracker> eventTrackerList = eventTrackerRepository.findAllByDeploymentId_SmartContractId(deployment.getSmartContractId());

        List<Disposable> disposables = activeDisposables.computeIfAbsent(
                deployment.getSmartContractAddress(),
                k -> new ArrayList<>()
        );

        eventTrackerList.forEach(eventTracker -> {
            OracleEventType oracleEventType = eventTracker.getOracleEventType();
            if (oracleEventType == null) {
                log.error("유효하지 않은 이벤트 타입입니다.");
                throw new IllegalArgumentException("유효하지 않은 이벤트 타입입니다.");
            }

            BigInteger startBlockNumber = eventTracker.getLastBlockNumber();
            if (startBlockNumber == null || startBlockNumber.compareTo(BigInteger.ZERO) < 0) {
                log.error("유효하지 않은 시작 블록 번호입니다: {}", startBlockNumber);
                throw new IllegalArgumentException("유효하지 않은 시작 블록 번호입니다: {}" + startBlockNumber);
            }

            log.info("Setting up event filter for contract: {}, event: {}, startBlockNumber: {}",
                    deployment.getSmartContractAddress(), oracleEventType, startBlockNumber);

            FractionalInvestmentToken contract = contractWrapper.getSmartContract(deployment.getSmartContractAddress());
            Disposable disposable = setEventFilter(contract, oracleEventType, startBlockNumber, deployment);
            if (disposable == null) {
                log.error("이벤트 필터 설정 실패: {} for contract: {}", oracleEventType, deployment.getSmartContractAddress());
                throw new RuntimeException("이벤트 필터 설정 실패 : " + oracleEventType);
            }

            disposables.add(disposable);
        });

        // 필터가 성공적으로 설정되면 재시도 횟수 초기화
        reconnectAttempts.remove(deployment.getSmartContractAddress());

        log.info("모든 이벤트 필터가 설정되었습니다: {}", deployment.getSmartContractAddress());
    }

    private Disposable setEventFilter(FractionalInvestmentToken contract, OracleEventType oracleEventType, BigInteger startBlockNumber, Deployment deployment) {
        try {
            Method smartContractEventMethod = contract.getClass().getMethod(
                    eventFunctionMap.get(oracleEventType).smartContractEventMethodName(),
                    DefaultBlockParameter.class,
                    DefaultBlockParameter.class
            );

            return ((Flowable<?>) smartContractEventMethod.invoke(
                    contract,
                    new DefaultBlockParameterNumber(startBlockNumber),
                    null
            )).subscribe(event -> {
                eventFunctionMap.get(oracleEventType).eventHandlerMethod().accept((BaseEventResponse) event);
            }, throwable -> {
                log.error("[Event Flowable Subscribe Error] {}", throwable.getMessage(), throwable);

                Throwable rootCause = throwable.getCause();
                if (throwable instanceof IOException || rootCause instanceof IOException) {
                    String address = deployment.getSmartContractAddress();
                    int attempt = reconnectAttempts.getOrDefault(address, 0) + 1; // 재연결 횟수
                    reconnectAttempts.put(address, attempt);

                    if (attempt > MAX_RETRIES) {
                        log.error("계약 {} 재연결 {}회 초과. 계약을 비활성화합니다.", address, MAX_RETRIES);
                        deactivateContract(deployment);
                        return;
                    }

                    int delay = Math.min(BASE_DELAY * (1 << (attempt - 1)), MAX_DELAY); // left shift를 통한 빠른 2배 연산

                    log.warn("WebSocket 끊김 감지. {}초 후 재연결 시도 ({}번째)", delay, attempt);

                    scheduler.schedule(() -> {
                        try {
                            setupAllEventFilter(deployment);
                        } catch (Exception e) {
                            log.error("재연결 실패: {}", e.getMessage(), e);
                        }
                    }, delay, TimeUnit.SECONDS);
                }
            });

        } catch (Exception e) {
            log.error("[Event Subscription Failed] {}", e.getMessage(), e);
            return null;
        }
    }

    @Transactional
    protected void deactivateContract(Deployment deployment) {
        String address = deployment.getSmartContractAddress();
        Integer attempts = reconnectAttempts.getOrDefault(address, 0);

        removeAllEventFilter(address);
        deployment.deactivate();
        deploymentRepository.save(deployment);
        reconnectAttempts.remove(address);

        log.error("계약 {} 가 {}회 이상 재연결 실패하여 비활성화되었습니다.", address, attempts);

        kafkaMessageProducer.sendContractConnectFailedEvent(deployment.getProjectId(), address, attempts);
    }

    private void removeAllEventFilter(String smartContractAddress) {
        List<Disposable> disposables = activeDisposables.remove(smartContractAddress);

        log.info("Removing event filters for contract: {}", smartContractAddress);

        if (disposables != null) {
            log.info("Removing event filters for contract: {}", smartContractAddress);
            for (Disposable disposable : disposables) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
        }
    }
}