package com.lzbz.auth.controller;

import com.lzbz.auth.annotation.RateLimit;
import com.lzbz.auth.dto.*;
import com.lzbz.auth.service.AuthService;
import com.lzbz.auth.service.TokenManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final TokenManagementService tokenManagementService;

    @PostMapping("/login")
    @RateLimit(type = "login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());
        return authService.login(request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Login successful for user: {}", request.getUsername()))
                .doOnError(error -> log.error("Login failed for user: {}, error: {}",
                        request.getUsername(), error.getMessage()));
    }

    @PostMapping("/register")
    @RateLimit(type = "register")
    public Mono<ResponseEntity<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        return authService.register(request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Registration successful for user: {}", request.getUsername()))
                .doOnError(error -> log.error("Registration failed for user: {}, error: {}",
                        request.getUsername(), error.getMessage()));
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(
            @RequestHeader("Authorization") String authHeader,
            Authentication authentication) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = authentication.getName();

            log.info("Logout attempt for user: {}", username);

            return authService.logout(token, username)
                    .then(Mono.just(ResponseEntity.ok().<Void>build()))
                    .doOnSuccess(v -> log.info("Logout successful for user: {}", username))
                    .doOnError(error -> log.error("Logout failed for user: {}, error: {}",
                            username, error.getMessage()));
        }

        return Mono.just(ResponseEntity.badRequest().build());
    }

    @PostMapping("/logout-all")
    public Mono<ResponseEntity<Void>> logoutAllSessions(Authentication authentication) {
        String username = authentication.getName();
        log.info("Logout all sessions attempt for user: {}", username);

        return authService.logoutAllSessions(username)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .doOnSuccess(v -> log.info("Logout all sessions successful for user: {}", username))
                .doOnError(error -> log.error("Logout all sessions failed for user: {}, error: {}",
                        username, error.getMessage()));
    }

    @PostMapping("/validate")
    public Mono<ResponseEntity<Boolean>> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return tokenManagementService.validateToken(token)
                    .map(ResponseEntity::ok)
                    .doOnError(error -> log.error("Token validation error: {}", error.getMessage()));
        }

        return Mono.just(ResponseEntity.badRequest().body(false));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO request) {
        return authService.refreshToken(request.getRefreshToken())
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
                .onErrorResume(e -> {
                    log.error("Error refreshing token: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }
}