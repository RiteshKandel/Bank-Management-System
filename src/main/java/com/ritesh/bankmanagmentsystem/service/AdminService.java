package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.dto.admin.AuditLogResponse;
import com.ritesh.bankmanagmentsystem.dto.loan.LoanResponse;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransactionResponse;
import com.ritesh.bankmanagmentsystem.dto.user.UserResponse;
import com.ritesh.bankmanagmentsystem.repository.AuditLogRepository;
import com.ritesh.bankmanagmentsystem.repository.BankTransactionRepository;
import com.ritesh.bankmanagmentsystem.repository.LoanRepository;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UserService userService;
    private final BankTransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;
    private final LoanRepository loanRepository;

    public AdminService(
        UserService userService,
        BankTransactionRepository transactionRepository,
        AuditLogRepository auditLogRepository,
        LoanRepository loanRepository
    ) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
        this.loanRepository = loanRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<UserResponse> users() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<TransactionResponse> transactions() {
        return transactionRepository.findAll().stream().map(tx -> new TransactionResponse(
            tx.getId(),
            tx.getReferenceNumber(),
            tx.getTransactionType(),
            tx.getAmount(),
            tx.getSourceAccount() == null ? null : tx.getSourceAccount().getId(),
            tx.getTargetAccount() == null ? null : tx.getTargetAccount().getId(),
            tx.getDescription(),
            tx.getPerformedByUserId(),
            tx.getCreatedAt()
        )).toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public List<LoanResponse> loans() {
        return loanRepository.findAllByOrderByCreatedAtDesc().stream().map(loan -> new LoanResponse(
            loan.getId(),
            loan.getUser().getId(),
            loan.getAccount() == null ? null : loan.getAccount().getId(),
            loan.getPrincipalAmount(),
            loan.getAnnualInterestRate(),
            loan.getTenureMonths(),
            loan.getMonthlyEmi(),
            loan.getStatus(),
            loan.getRemarks(),
            loan.getReviewedBy(),
            loan.getReviewedAt(),
            loan.getCreatedAt()
        )).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogResponse> audits() {
        return auditLogRepository.findAll().stream().map(audit -> new AuditLogResponse(
            audit.getId(),
            audit.getActorUserId(),
            audit.getAction(),
            audit.getResourceType(),
            audit.getResourceId(),
            audit.getDetails(),
            audit.getIpAddress(),
            audit.getCreatedAt()
        )).toList();
    }
}

