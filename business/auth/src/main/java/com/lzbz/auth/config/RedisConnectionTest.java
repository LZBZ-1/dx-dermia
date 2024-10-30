package com.lzbz.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisConnectionTest implements CommandLineRunner {

    private final ReactiveStringRedisTemplate redisTemplate;

    @Override
    public void run(String... args) {
        log.info("Testing Redis connection...");

        redisTemplate.opsForValue()
                .set("test:key", "Hello Redis Cloud!")
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(result -> log.info("Successfully connected to Redis!"))
                .doOnError(error -> log.error("Failed to connect to Redis: {}", error.getMessage()))
                .onErrorResume(e -> Mono.empty())
                .subscribe();
    }
}