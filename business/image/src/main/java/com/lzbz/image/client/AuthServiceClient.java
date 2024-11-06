package com.lzbz.image.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceClient {
    private final WebClient authServiceWebClient;

    public Mono<Long> getUserIdFromUsername(String username) {
        log.info("Requesting userId for username: {}", username);
        return authServiceWebClient.get()
                .uri("/api/users/by-username/{username}", username)
                .retrieve()
                .bodyToMono(Long.class)
                .doOnSuccess(userId -> log.info("Successfully retrieved userId: {} for username: {}", userId, username))
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.error("User not found: {}", username);
                        return Mono.error(new RuntimeException("User not found"));
                    }
                    log.error("Error calling auth service: {} - {}", e.getStatusCode(), e.getMessage());
                    return Mono.error(new RuntimeException("Error calling auth service"));
                })
                .onErrorResume(e -> {
                    log.error("Unexpected error: {}", e.getMessage());
                    return Mono.error(new RuntimeException("Unexpected error getting userId"));
                });
    }
}