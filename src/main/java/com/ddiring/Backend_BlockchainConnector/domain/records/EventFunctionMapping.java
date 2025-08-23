package com.ddiring.Backend_BlockchainConnector.domain.records;

import org.web3j.protocol.core.methods.response.BaseEventResponse;

import java.util.function.Consumer;

public record EventFunctionMapping(
        String smartContractEventMethodName,
        Consumer<BaseEventResponse> eventHandlerMethod
) {
}
