package com.ddiring.Backend_BlockchainConnector.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "blockchain")
public class BlockchainProperties {
    private Etherscan etherscan;
    private Sepolia sepolia;
    private Admin admin;
    private Web3j web3j;

    @Getter
    @Setter
    public static class Etherscan {
        private Api api;
        @Getter
        @Setter
        public static class Api {
            private String key;
        }
    }

    @Getter
    @Setter
    public static class Sepolia {
        private Rpc rpc;
        @Getter
        @Setter
        public static class Rpc {
            private String url;
        }
    }

    @Getter
    @Setter
    public static class Admin {
        private Key key;
        @Getter
        @Setter
        public static class Key {
            private String privateKey;
        }
    }

    @Getter
    @Setter
    public static class Web3j {
        private Long pollingInterval;
    }
}
