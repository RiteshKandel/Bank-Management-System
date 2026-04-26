package com.ritesh.bankmanagmentsystem.dto.user;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
    Long id,
    String fullName,
    String email,
    boolean enabled,
    Set<String> roles,
    Instant createdAt
) {
}

