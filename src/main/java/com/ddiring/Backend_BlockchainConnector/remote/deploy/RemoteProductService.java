package com.ddiring.Backend_BlockchainConnector.remote.deploy;

import com.ddiring.Backend_BlockchainConnector.remote.deploy.dto.UpdateContractAddressDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "RemoteProductService", path = "/api/product", url = "http://localhost:8081")
public interface RemoteProductService {
    @PostMapping("/update/contract")
    ResponseEntity<Void> setContractAddress(@RequestBody UpdateContractAddressDto updateContractAddressDto);
}
