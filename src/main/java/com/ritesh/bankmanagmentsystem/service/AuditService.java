package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.entity.AuditLog;
import com.ritesh.bankmanagmentsystem.repository.AuditLogRepository;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long actorUserId, String action, String resourceType, String resourceId, String details, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setActorUserId(actorUserId);
        log.setAction(action);
        log.setResourceType(resourceType);
        log.setResourceId(resourceId);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        log.setCreatedAt(Instant.now());
        auditLogRepository.save(log);
    }
}

