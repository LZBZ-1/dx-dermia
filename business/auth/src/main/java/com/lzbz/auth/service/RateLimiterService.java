package com.lzbz.auth.service;

import com.lzbz.auth.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimiterService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final RateLimitProperties properties;
    private static final String RATE_LIMITER_PREFIX = "rate:limiter:";

    public Mono<Boolean> isAllowed(String key, String type) {
        String rateLimiterKey = RATE_LIMITER_PREFIX + type + ":" + key;

        int maxAttempts = getMaxAttempts(type);
        int duration = getDuration(type);

        return redisTemplate.opsForValue().increment(rateLimiterKey)
                .flatMap(attempts -> {
                    if (attempts == 1) {
                        return redisTemplate.expire(rateLimiterKey, Duration.ofSeconds(duration))
                                .thenReturn(true);
                    }

                    if (attempts > maxAttempts) {
                        return redisTemplate.expire(rateLimiterKey, Duration.ofSeconds(properties.getBlockDuration()))
                                .thenReturn(false);
                    }

                    return Mono.just(true);
                })
                .doOnSuccess(allowed -> {
                    if (!allowed) {
                        log.warn("Rate limit exceeded for key: {} and type: {}", key, type);
                    }
                });
    }

    private int getMaxAttempts(String type) {
        switch (type) {
            case "login":
                return properties.getLogin().getMaxAttempts();
            case "register":
                return properties.getRegister().getMaxAttempts();
            default:
                return properties.getMaxAttempts();
        }
    }

    private int getDuration(String type) {
        switch (type) {
            case "login":
                return properties.getLogin().getDuration();
            case "register":
                return properties.getRegister().getDuration();
            default:
                return properties.getDuration();
        }
    }

    public Mono<Void> resetLimit(String key, String type) {
        return redisTemplate.delete(RATE_LIMITER_PREFIX + type + ":" + key)
                .then();
    }
}