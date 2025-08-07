package com.ddiring.Backend_BlockchainConnector.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jenkins")
public class JenkinsProperties {
    private String url;
    private String username;
    private String apiToken;
    private String authenticationToken;
    private String job;
}