// src/main/java/com/lzbz/auth/dto/TokenValidationResult.java
package com.lzbz.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenValidationResult {
    private boolean isValid;
    private boolean hasActiveSessions;
    private String message;
}