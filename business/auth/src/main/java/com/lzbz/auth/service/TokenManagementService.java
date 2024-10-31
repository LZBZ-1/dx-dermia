package com.lzbz.auth.service;

import com.lzbz.auth.config.TokenProperties;
import com.lzbz.auth.dto.TokenValidationResult;
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
    private static final String REFRESH_TOKEN_PREFIX = "refresh:token:";
    private static final String ACTIVE_SESSIONS_PREFIX = "active:sessions:";  // Nuevo

    // Nuevo mét-odo para guardar un token activo
    public Mono<Void> saveActiveToken(String token, String username) {
        String userTokensKey = USER_TOKENS_PREFIX + username;
        String activeSessionKey = ACTIVE_SESSIONS_PREFIX + username;

        return redisTemplate.opsForSet().add(userTokensKey, token)
                .then(redisTemplate.opsForSet().add(activeSessionKey, token))
                .then(redisTemplate.expire(activeSessionKey,
                        Duration.ofMillis(tokenProperties.getAccessTokenExpirationMs())))
                .doOnSuccess(v -> log.debug("Token saved as active for user: {}", username))
                .then();
    }

    public Mono<Void> blacklistToken(String token, String username) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        String activeSessionKey = ACTIVE_SESSIONS_PREFIX + username;

        return redisTemplate.opsForValue()
                .set(blacklistKey, "true",
                        Duration.ofMillis(tokenProperties.getAccessTokenExpirationMs()))
                .then(redisTemplate.opsForSet().remove(activeSessionKey, token))
                .doOnSuccess(v -> log.info("Token blacklisted for user: {}", username))
                .doOnError(e -> log.error("Error blacklisting token for user: {}", username, e))
                .then();
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
        return redisTemplate.hasKey(blacklistKey);
    }

    public Mono<Void> blacklistAllUserTokens(String username) {
        String activeSessionKey = ACTIVE_SESSIONS_PREFIX + username;
        String userTokensKey = USER_TOKENS_PREFIX + username;

        // 1. Obtener todos los tokens activos
        return redisTemplate.opsForSet().members(activeSessionKey)
                .flatMap(token -> {
                    String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
                    return redisTemplate.opsForValue()
                            .set(blacklistKey, "true",
                                    Duration.ofMillis(tokenProperties.getAccessTokenExpirationMs()));
                })
                // 2. Eliminar refresh tokens
                .thenMany(redisTemplate.scan()
                        .filter(key -> key.startsWith(REFRESH_TOKEN_PREFIX))
                        .flatMap(key -> redisTemplate.opsForValue().get(key)
                                .filter(value -> value.equals(username))
                                .flatMap(value -> redisTemplate.delete(key))))
                // 3. Limpiar sesiones activas
                .then(redisTemplate.delete(activeSessionKey))
                // 4. Limpiar registro de tokens
                .then(redisTemplate.delete(userTokensKey))
                .doOnSuccess(v -> log.info("All sessions terminated for user: {}", username))
                .doOnError(e -> log.error("Error in blacklistAllUserTokens: {}", e.getMessage())).then();
    }

    public Mono<Boolean> hasActiveTokens(String username) {
        String activeSessionKey = ACTIVE_SESSIONS_PREFIX + username;
        return redisTemplate.opsForSet().size(activeSessionKey)
                .map(size -> size > 0)
                .defaultIfEmpty(false)
                .doOnSuccess(hasActive -> log.debug("Active sessions for {}: {}", username, hasActive));
    }

    // Los métodos de refresh token se mantienen igual
    public Mono<Boolean> saveRefreshToken(String refreshToken, String username) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.opsForValue()
                .set(key, username, Duration.ofMillis(tokenProperties.getRefreshTokenExpirationMs()));
    }

    public Mono<Boolean> isRefreshTokenValid(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.hasKey(key);
    }

    public Mono<Void> invalidateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.delete(key).then();
    }

    public Mono<String> getUsernameFromRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        return redisTemplate.opsForValue().get(key);
    }

    public Mono<Long> getActiveSessionsCount(String username) {
        String activeSessionKey = ACTIVE_SESSIONS_PREFIX + username;
        return redisTemplate.opsForSet().size(activeSessionKey)
                .defaultIfEmpty(0L)
                .doOnSuccess(count -> log.debug("Active session count for {}: {}", username, count));
    }

    public Mono<Boolean> validateToken(String token) {
        return isTokenBlacklisted(token)
                .map(isBlacklisted -> !isBlacklisted) // Si no está en blacklist, es válido
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

    public Mono<TokenValidationResult> validateTokenWithDetails(String token, String username) {
        return Mono.zip(
                isTokenBlacklisted(token),
                hasActiveTokens(username)
        ).map(tuple -> {
            boolean isBlacklisted = tuple.getT1();
            boolean hasActiveSessions = tuple.getT2();
            return new TokenValidationResult(
                    !isBlacklisted,
                    hasActiveSessions,
                    isBlacklisted ? "Token is blacklisted" : "Token is valid"
            );
        });
    }
}