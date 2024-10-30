package com.lzbz.auth.controller;

import com.lzbz.auth.dto.AuthRequestDTO;
import com.lzbz.auth.dto.AuthResponseDTO;
import com.lzbz.auth.dto.RegisterRequestDTO;
import com.lzbz.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponseDTO>> login(@Valid @RequestBody AuthRequestDTO request) {
        log.info("Login attempt for user: {}", request.getUsername());
        return authService.login(request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Login successful for user: {}", request.getUsername()))
                .doOnError(error -> log.error("Login failed for user: {}, error: {}",
                        request.getUsername(), error.getMessage()));
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        log.info("Registration attempt for user: {}", request.getUsername());
        return authService.register(request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Registration successful for user: {}", request.getUsername()))
                .doOnError(error -> log.error("Registration failed for user: {}, error: {}",
                        request.getUsername(), error.getMessage()));
    }
}
