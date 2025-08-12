package com.ddiring.Backend_BlockchainConnector.service.dto;

import com.ddiring.Backend_BlockchainConnector.config.BlockchainProperties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DynamicGasProvider;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class ContractWrapper {
    private final BlockchainProperties blockchainProperties;

    Web3j web3j;
    Credentials credentials;
    DynamicGasProvider gasProvider;

    // @PostConstruct는 의존성 주입이 이루어진 후 초기화를 수행하는 메서드
    @PostConstruct
    private void init() {
        try {
            String rpcUrl = blockchainProperties.getSepolia().getRpc().getUrl();
            if (rpcUrl == null || rpcUrl.isEmpty()) {
                throw new IllegalArgumentException("RPC URL이 설정되지 않았습니다.");
            }

            String privateKey = blockchainProperties.getAdmin().getKey().getPrivateKey();
            if (privateKey == null || privateKey.isEmpty()) {
                throw new IllegalArgumentException("Admin private key가 설정되지 않았습니다.");
            }

            this.web3j = Web3j.build(new HttpService(rpcUrl));
            this.credentials = Credentials.create(privateKey);
            this.gasProvider = new DynamicGasProvider(web3j);
        } catch (IllegalArgumentException e) {
            log.error("블록체인 설정 오류: {}", e.getMessage());
            throw new RuntimeException("Web3j 설정 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Web3j 초기화 중 알 수 없는 오류 발생: ", e);
            throw new RuntimeException("Web3j 초기화 실패: " + e.getMessage(), e);
        }
    }

}