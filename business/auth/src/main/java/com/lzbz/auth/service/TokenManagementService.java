package com.lzbz.auth.service;

import com.lzbz.auth.config.TokenProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenManagementService {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final TokenProperties tokenProperties;
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_TOKENS_PREFIX = "user:tokens:";

    public Mono<Void> blacklistToken(String token, String username) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        String userTokensKey = USER_TOKENS_PREFIX + username;

        return redisTemplate.opsForValue()
                .set(blacklistKey, "true",
                        Duration.ofMillis(tokenProperties.getAccessTokenExpirationMs()))
                .doOnSuccess(success -> log.debug("Token added to blacklist: {}", blacklistKey))
                .then(redisTemplate.opsForSet().add(userTokensKey, token))
                .doOnSuccess(success -> log.debug("Token added to user tokens set: {}", userTokensKey))
                .then()
                .doOnSuccess(v -> log.info("Token blacklisted for user: {}", username))
                .doOnError(e -> log.error("Error blacklisting token for user: {}, error: {}",
                        username, e.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .doBeforeRetry(signal -> log.warn("Retrying Redis operation after error: {}",
                                signal.failure().getMessage())));
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(blacklistKey)
                .doOnSuccess(exists -> {
                    if (exists) {
                        log.debug("Token found in blacklist: {}", blacklistKey);
                    }
                })
                .doOnError(e -> log.error("Error checking token blacklist: {}, error: {}",
                        blacklistKey, e.getMessage()))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(100)));
    }

    public Mono<Void> blacklistAllUserTokens(String username) {
        String userTokensKey = USER_TOKENS_PREFIX + username;

        return redisTemplate.opsForSet().members(userTokensKey)
                .doOnNext(token -> log.debug("Processing token for blacklisting: {}", token))
                .flatMap(token -> {
                    String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
                    return redisTemplate.opsForValue()
                            .set(blacklistKey, "true",
                                    Duration.ofMillis(tokenProperties.getAccessTokenExpirationMs()))
                            .doOnSuccess(success -> log.debug("Token blacklisted: {}", blacklistKey));
                })
                .then(redisTemplate.delete(userTokensKey))
                .doOnSuccess(v -> log.info("All tokens blacklisted for user: {}", username))
                .doOnError(e -> log.error("Error blacklisting all tokens for user: {}, error: {}",
                        username, e.getMessage()))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .doBeforeRetry(signal -> log.warn("Retrying Redis operation after error: {}",
                                signal.failure().getMessage())))
                .then();
    }

    public Mono<Boolean> validateToken(String token) {
        return isTokenBlacklisted(token)
                .map(blacklisted -> !blacklisted)
                .doOnSuccess(valid -> {
                    if (!valid) {
                        log.debug("Token validation failed - token is blacklisted: {}", token);
                    } else {
                        log.debug("Token validation successful: {}", token);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error validating token: {}, error: {}", token, e.getMessage());
                    return Mono.just(false);
                });
    }
}