package com.ddiring.Backend_BlockchainConnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class BackendBlockchainConnectorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendBlockchainConnectorApplication.class, args);
	}

}
