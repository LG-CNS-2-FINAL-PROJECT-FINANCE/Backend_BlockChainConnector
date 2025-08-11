package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import io.reactivex.disposables.Disposable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.DefaultBlockParameterName;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractEventService {
    private final Map<String, List<Disposable>> activeDisposables = new ConcurrentHashMap<>();

    private final ContractWrapper contractWrapper;

    @PostConstruct
    public void setupAllEventFilters() {
        // TODO: DB에서 계약 주소를 조회하는 로직 추가
        // 예시로 하드코딩된 계약 주소를 사용합니다.
        // Dto 구성 必 -> { 컨트랙트 주소, 마지막 블록 번호, 컨트랙트 상태 등 }
        List<String> contractAddresses = List.of("0xf829ec1d09c6d9ac97489096b85277012eb56e74", "0xb1b659696049b455540056c0bdeef76de09cd92f");

        for (String address : contractAddresses) {
            setupEventFilter(address);
        }
    }

    public void addEventFilter(String contractAddress) {
        // TODO: DB 에서 계약이 유효한지 확인하는 로직 추가

        // TODO: 중복 체크 로직 추가
        if (activeDisposables.containsKey(contractAddress)) {
            throw new IllegalArgumentException("이미 이벤트 필터가 등록되어 있습니다: " + contractAddress);
        }

        // TODO: DB에 저장하는 로직 추가

        setupEventFilter(contractAddress);
    }

    public void removeEventFilter(String contractAddress) {
        List<Disposable> disposables = activeDisposables.remove(contractAddress);

        if (disposables != null && !disposables.isEmpty()) {
            // Todo: DB에서 상태 변경하는 로직 추가

            for (Disposable disposable : disposables) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
        }
    }

    private void setupEventFilter(String contractAddress) {
        FractionalInvestmentToken myContract = FractionalInvestmentToken.load(
                contractAddress,
                contractWrapper.getWeb3j(),
                contractWrapper.getCredentials(),
                contractWrapper.getGasProvider()
        );

        // TODO: From Block 번호 DB 값으로 수정
        Disposable investmentSuccessEvent = myContract.investmentSuccessfulEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(this::handleInvestmentSuccess);

        Disposable investmentFailedEvent = myContract.investmentFailedEventFlowable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
                .subscribe(this::handleInvestmentFailure);

        activeDisposables.put(contractAddress, List.of(investmentSuccessEvent, investmentFailedEvent));
    }

    private void handleInvestmentSuccess(FractionalInvestmentToken.InvestmentSuccessfulEventResponse event) {
        log.info("Investment 성공: {}", Arrays.toString(event.projectId));
    }

    private void handleInvestmentFailure(FractionalInvestmentToken.InvestmentFailedEventResponse event) {
        log.info("Investment 실패: {}, 사유: {}", Arrays.toString(event.projectId), event.reason);
    }
}
