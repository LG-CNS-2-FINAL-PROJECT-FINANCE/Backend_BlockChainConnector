package com.ddiring.Backend_BlockchainConnector.remote.log;

import com.ddiring.Backend_BlockchainConnector.domain.dto.EtherscanEventLogDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "RemoteEtherscanService", url = "https://api.etherscan.io")
public interface RemoteEtherscanService {

    @GetMapping(value = "/v2/api")
    EtherscanEventLogDto.Response getLogsFromEtherScan(
            @RequestParam("chainid") Long chainId,
            @RequestParam("module") String module,
            @RequestParam("action") String action,
            @RequestParam("contractaddress") String contractAddress,
            @RequestParam("address") String address,
            @RequestParam("page") Long page,
            @RequestParam("offset") Long offset,
            @RequestParam("startblock") Long startBlock,
            @RequestParam("endblock") Long endBlock,
            @RequestParam("sort") String sort,
            @RequestParam("apikey") String apiKey
    );
}
