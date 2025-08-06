package com.ddiring.Backend_BlockchainConnector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // 이 클래스가 스프링 설정 클래스임을 명시
public class RestTemplateConfig {

    @Bean // 이 메서드가 반환하는 객체를 스프링 컨테이너에 Bean으로 등록
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

