package com.ddiring.Backend_BlockchainConnector.service;

import com.ddiring.Backend_BlockchainConnector.domain.dto.TradeDto;
import com.ddiring.Backend_BlockchainConnector.service.dto.ContractWrapper;
import com.ddiring.contract.FractionalInvestmentToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmartContractTradeService {
    private final ContractWrapper contractWrapper;

    public void trade(TradeDto tradeDto) {
        try {
            FractionalInvestmentToken smartContract = FractionalInvestmentToken.load(
                    tradeDto.getSmartContractAddress(),
                    contractWrapper.getWeb3j(),
                    contractWrapper.getCredentials(),
                    contractWrapper.getGasProvider()
            );

            // TODO: 거래 요청 로직 추가

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
