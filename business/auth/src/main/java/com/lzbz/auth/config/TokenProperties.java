package com.lzbz.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.token")
public class TokenProperties {
    private long accessTokenExpirationMs = 3600000; // 1 hora
    private long refreshTokenExpirationMs = 86400000; // 24 horas
}