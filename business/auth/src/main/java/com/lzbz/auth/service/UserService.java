package com.lzbz.auth.service;

import com.lzbz.auth.dto.UserDTO;
import com.lzbz.auth.model.Role;
import com.lzbz.auth.model.User;
import com.lzbz.auth.repository.RoleRepository;
import com.lzbz.auth.repository.UserRepository;
import com.lzbz.auth.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public Flux<UserDTO> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .flatMap(user -> getUserRoles(user.getUserId())
                        .map(roles -> UserDTO.builder()
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .isActive(user.isActive())
                                .lastLogin(user.getLastLogin())
                                .roles(roles)
                                .build()));
    }

    public Mono<UserDTO> getUserById(Long userId) {
        log.info("Fetching user by ID: {}", userId);
        return userRepository.findById(userId)
                .flatMap(user -> getUserRoles(userId)
                        .map(roles -> UserDTO.builder()
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .isActive(user.isActive())
                                .lastLogin(user.getLastLogin())
                                .roles(roles)
                                .build()));
    }

    private Mono<List<String>> getUserRoles(Long userId) {
        return userRoleRepository.findByUserId(userId)
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()))
                .map(Role::getRoleName)
                .collectList();
    }

    public Mono<Long> getUserIdByUsername(String username) {
        log.info("Getting userId for username: {}", username);
        return userRepository.findByUsername(username)
                .map(User::getUserId)
                .doOnSuccess(userId -> log.debug("Found userId: {} for username: {}", userId, username))
                .doOnError(error -> log.error("Error finding userId for username {}: {}",
                        username, error.getMessage()));
    }
}
