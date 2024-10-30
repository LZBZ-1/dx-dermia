package com.lzbz.auth.service;

import com.lzbz.auth.dto.AuthRequestDTO;
import com.lzbz.auth.dto.AuthResponseDTO;
import com.lzbz.auth.dto.RegisterRequestDTO;
import com.lzbz.auth.dto.TokenResponse;
import com.lzbz.auth.model.Role;
import com.lzbz.auth.model.User;
import com.lzbz.auth.model.UserRole;
import com.lzbz.auth.repository.RoleRepository;
import com.lzbz.auth.repository.UserRepository;
import com.lzbz.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenManagementService tokenManagementService;

    public Mono<AuthResponseDTO> login(AuthRequestDTO request) {
        log.info("Attempting login for user: {}", request.getUsername());
        return userRepository.findByUsername(request.getUsername())
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
                .flatMap(user -> updateLastLogin(user)
                        .then(getUserRoles(user))
                        .map(roles -> createAuthResponse(user, roles)))
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")));
    }

    public Mono<AuthResponseDTO> register(RegisterRequestDTO request) {
        log.info("Processing registration for user: {}", request.getUsername());
        return userRepository.existsByUsername(request.getUsername())
                .flatMap(exists -> exists
                        ? Mono.error(new RuntimeException("Username already exists"))
                        : createUser(request))
                .flatMap(user -> assignDefaultRole(user)
                        .then(getUserRoles(user))
                        .map(roles -> createAuthResponse(user, roles)));
    }

    private Mono<User> createUser(RegisterRequestDTO request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
        return userRepository.save(user);
    }

    private Mono<Void> assignDefaultRole(User user) {
        return roleRepository.findByRoleName("patient")
                .map(role -> UserRole.builder()
                        .userId(user.getUserId())
                        .roleId(role.getRoleId())
                        .assignedAt(ZonedDateTime.now())
                        .build())
                .flatMap(userRoleRepository::save)
                .then();
    }

    private Mono<List<String>> getUserRoles(User user) {
        return userRoleRepository.findByUserId(user.getUserId())
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
                .map(Role::getRoleName)
                .collectList();
    }

    private Mono<User> updateLastLogin(User user) {
        user.setLastLogin(ZonedDateTime.now());
        return userRepository.save(user);
    }

    private AuthResponseDTO createAuthResponse(User user, List<String> roles) {
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        // Guardar el refresh token en Redis
        tokenManagementService.saveRefreshToken(refreshToken, user.getUsername()).subscribe();

        return AuthResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    public Mono<Void> logout(String token, String username) {
        log.info("Processing logout for user: {}", username);
        return tokenManagementService.blacklistToken(token, username)
                .doOnSuccess(v -> log.info("Logout successful for user: {}", username))
                .doOnError(e -> log.error("Logout failed for user: {}", username, e));
    }

    public Mono<Void> logoutAllSessions(String username) {
        log.info("Processing logout for all sessions of user: {}", username);
        return tokenManagementService.blacklistAllUserTokens(username)
                .doOnSuccess(v -> log.info("All sessions logged out for user: {}", username))
                .doOnError(e -> log.error("Logout all sessions failed for user: {}", username, e));
    }

    public Mono<AuthResponseDTO> refreshToken(String refreshToken) {
        return jwtService.validateRefreshToken(refreshToken)
                .filter(valid -> valid)
                .flatMap(valid -> tokenManagementService.getUsernameFromRefreshToken(refreshToken))
                .flatMap(username -> {
                    // Generar nuevos tokens
                    String newAccessToken = jwtService.generateToken(username);
                    String newRefreshToken = jwtService.generateRefreshToken(username);

                    // Invalidar el refresh token anterior y guardar el nuevo
                    return tokenManagementService.invalidateRefreshToken(refreshToken)
                            .then(tokenManagementService.saveRefreshToken(newRefreshToken, username))
                            .thenReturn(AuthResponseDTO.builder()
                                    .token(newAccessToken)
                                    .refreshToken(newRefreshToken)
                                    .username(username)
                                    .expiresIn(jwtService.getExpirationTime())
                                    .build());
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")));
    }
}
