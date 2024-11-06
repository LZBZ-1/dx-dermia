// WebClientConfig.java
package com.lzbz.image.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient authServiceWebClient(WebClient.Builder builder, AuthProperties authProperties) {
        return builder
                .baseUrl(authProperties.getUrl())
                .build();
    }
}