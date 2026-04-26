package com.ritesh.bankmanagmentsystem.dto.admin;

import java.time.Instant;

public record AuditLogResponse(
    Long id,
    Long actorUserId,
    String action,
    String resourceType,
    String resourceId,
    String details,
    String ipAddress,
    Instant createdAt
) {
}

