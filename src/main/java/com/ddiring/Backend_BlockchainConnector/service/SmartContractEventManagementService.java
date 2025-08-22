package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.entity.EventTracker;
import com.ddiring.Backend_BlockchainConnector.domain.entity.SmartContract;
import com.ddiring.Backend_BlockchainConnector.domain.enums.EventType;
import com.ddiring.Backend_BlockchainConnector.domain.records.EventFunctionMapping;
import com.ddiring.Backend_BlockchainConnector.repository.EventTrackerRepository;
import com.ddiring.Backend_BlockchainConnector.repository.SmartContractRepository;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractEventManagementService {
    private final SmartContractEventProcessorService eventProcessorService;

    private final Map<String, List<Disposable>> activeDisposables = new ConcurrentHashMap<>();
    private final Map<EventType, EventFunctionMapping> eventFunctionMap = new EnumMap<>(EventType.class);

    private final SmartContractRepository smartContractRepository;
    private final EventTrackerRepository eventTrackerRepository;

    private final ContractWrapper contractWrapper;

    @PostConstruct
    public void init() {
        initEventFunctionMap();

        List<SmartContract> smartContractList = smartContractRepository.findAllByIsActive(true);
        for (SmartContract contract : smartContractList) {
            setupAllEventFilter(contract);
        }
    }

    private void initEventFunctionMap() {
        eventFunctionMap.put(
                EventType.INVESTMENT_SUCCESSFUL,
                new EventFunctionMapping(
                        "investmentSuccessfulEventFlowable",
                        event -> eventProcessorService.handleInvestmentSuccessful((FractionalInvestmentToken.InvestmentSuccessfulEventResponse) event)
                )
        );
        eventFunctionMap.put(
                EventType.INVESTMENT_FAILED,
                new EventFunctionMapping(
                        "investmentFailedEventFlowable",
                        event -> eventProcessorService.handleInvestmentFailed((FractionalInvestmentToken.InvestmentFailedEventResponse) event)
                )
        );
        eventFunctionMap.put(
                EventType.TRADE_SUCCESSFUL,
                new EventFunctionMapping(
                        "tradeSuccessfulEventFlowable",
                        event -> eventProcessorService.handleTradeSuccessful((FractionalInvestmentToken.TradeSuccessfulEventResponse) event)
                )
        );
        eventFunctionMap.put(
                EventType.TRADE_FAILED,
                new EventFunctionMapping(
                        "tradeFailedEventFlowable",
                        event -> eventProcessorService.handleTradeFailed((FractionalInvestmentToken.TradeFailedEventResponse) event)
                )
        );
    }

    @Transactional
    public void addSmartContract(String projectId, String smartContractAddress, BigInteger blockNumber) {
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

        if (smartContractRepository.existsBySmartContractAddressOrProjectId(smartContractAddress, projectId)) {
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
            SmartContract contract = SmartContract.builder()
                    .projectId(projectId)
                    .smartContractAddress(smartContractAddress)
                    .isActive(true)
                    .build();
            smartContractRepository.save(contract);

            // 이벤트 필터 설정
            log.info("Adding new smart contract: {}", smartContractAddress);
            List<EventTracker> eventTrackers = new ArrayList<>();
            EventType.getAllEvent().forEach(event -> {
                EventTracker eventTracker = EventTracker.builder()
                        .smartContractId(contract)
                        .eventType(event)
                        .lastBlockNumber(blockNumber)
                        .build();
                eventTrackers.add(eventTracker);
                log.info("Adding event tracker for contract: {}, event: {}", smartContractAddress, event.getEventName());
            });
            eventTrackerRepository.saveAll(eventTrackers);

            setupAllEventFilter(contract);
        } catch (Exception e) {
            log.error("[스마트 컨트랙트 등록 실패] {}", e.getMessage());
            throw new RuntimeException("[스마트 컨트랙트 등록 실패] " + e.getMessage());
        }
    }

    @Transactional
    public void removeSmartContract(SmartContract contract) {
        if (contract == null || contract.getSmartContractAddress() == null) {
            throw new IllegalArgumentException("유효하지 않은 계약 정보입니다.");
        }

        if (contract.getIsActive() == null || !contract.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계약입니다.");
        }

        if (!activeDisposables.containsKey(contract.getSmartContractAddress())) {
            throw new IllegalArgumentException("등록된 이벤트 필터가 없습니다: " + contract.getSmartContractAddress());
        }

        if (!smartContractRepository.existsById(contract.getSmartContractId())) {
            throw new IllegalArgumentException("해당 계약이 존재하지 않습니다: " + contract.getSmartContractAddress());
        }

        // 계약 비활성화
        log.info("Deactivating contract: {}", contract.getSmartContractAddress());
        contract.deactivate();
        smartContractRepository.save(contract);

        // 계약의 모든 이벤트 필터 제거
        List<Disposable> disposables = activeDisposables.remove(contract.getSmartContractAddress());
        log.info("Removing event filters for contract: {}", contract.getSmartContractAddress());
        for (Disposable disposable : disposables) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }

    private void setupAllEventFilter(SmartContract contract) {
        if (contract == null || contract.getSmartContractAddress() == null) {
            throw new IllegalArgumentException("유효하지 않은 계약 정보입니다.");
        }

        if (contract.getIsActive() == null || !contract.getIsActive()) {
            throw new IllegalArgumentException("비활성화된 계약입니다.");
        }

        List<EventTracker> eventTrackerList = eventTrackerRepository.findAllBySmartContractId_SmartContractId(contract.getSmartContractId());

        FractionalInvestmentToken myContract = FractionalInvestmentToken.load(
                contract.getSmartContractAddress(),
                contractWrapper.getWeb3j(),
                contractWrapper.getCredentials(),
                contractWrapper.getGasProvider()
        );

        List<Disposable> disposables = new ArrayList<>();
        eventTrackerList.forEach(eventTracker -> {
            EventType eventType = eventTracker.getEventType();
            if (eventType == null) {
                log.error("유효하지 않은 이벤트 타입입니다.");
                return;
            }

            BigInteger startBlockNumber = eventTracker.getLastBlockNumber();
            if (startBlockNumber == null || startBlockNumber.compareTo(BigInteger.ZERO) < 0) {
                log.error("유효하지 않은 시작 블록 번호입니다: {}", startBlockNumber);
                return;
            }

            log.info("Setting up event filter for contract: {}, event: {}, startBlockNumber: {}",
                    contract.getSmartContractAddress(), eventType, startBlockNumber);

            Disposable disposable = setEventFilter(myContract, eventType, startBlockNumber);
            if (disposable == null) {
                log.error("이벤트 필터 설정 실패: {} for contract: {}", eventType, contract.getSmartContractAddress());
                return;
            }

            disposables.add(disposable);
        });

        log.info("모든 이벤트 필터가 설정되었습니다: {}", contract.getSmartContractAddress());

        activeDisposables.put(contract.getSmartContractAddress(), disposables);
    }

    private Disposable setEventFilter(FractionalInvestmentToken contract, EventType eventType, BigInteger startBlockNumber) {
        try {
            Method smartContractEventMethod = contract.getClass().getMethod(
                    eventFunctionMap.get(eventType).smartContractEventMethodName(),
                    DefaultBlockParameter.class,
                    DefaultBlockParameter.class
            );

            return ((Flowable<?>) smartContractEventMethod.invoke(
                    contract,
                    new DefaultBlockParameterNumber(startBlockNumber),
                    DefaultBlockParameterName.LATEST
            )).subscribe(event -> {
                eventFunctionMap.get(eventType).eventHandlerMethod().accept((BaseEventResponse) event);
            });

        } catch (NoSuchMethodException e) {
            log.error("[NoSuchMethodException] {}", e.getMessage());
            return null;
        } catch (SecurityException e) {
            log.error("[SecurityException] {}", e.getMessage());
            return null;
        } catch (IllegalAccessException e) {
            log.error("[IllegalAccessException] {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            log.error("[IllegalArgumentException] {}", e.getMessage());
            return null;
        } catch (InvocationTargetException e) {
            log.error("[InvocationTargetException] {}", e.getMessage());
            return null;
        }
    }
}
