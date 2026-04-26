package com.ritesh.bankmanagmentsystem.repository;

import com.ritesh.bankmanagmentsystem.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}

