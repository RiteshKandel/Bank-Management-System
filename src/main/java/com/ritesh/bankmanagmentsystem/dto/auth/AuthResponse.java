package com.ritesh.bankmanagmentsystem.dto.auth;

import java.util.Set;

public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String fullName,
    String email,
    Set<String> roles
) {
}

