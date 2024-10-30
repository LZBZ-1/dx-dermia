package com.lzbz.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {
    private int maxAttempts = 5;
    private int duration = 60;  // en segundos
    private int blockDuration = 900;  // en segundos

    private LoginLimitConfig login = new LoginLimitConfig();
    private RegisterLimitConfig register = new RegisterLimitConfig();

    @Data
    public static class LoginLimitConfig {
        private int maxAttempts = 5;
        private int duration = 60;
    }

    @Data
    public static class RegisterLimitConfig {
        private int maxAttempts = 3;
        private int duration = 300;
    }
}