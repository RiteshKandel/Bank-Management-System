package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.dto.loan.LoanApplyRequest;
import com.ritesh.bankmanagmentsystem.dto.loan.LoanDecisionRequest;
import com.ritesh.bankmanagmentsystem.dto.loan.LoanResponse;
import com.ritesh.bankmanagmentsystem.entity.Account;
import com.ritesh.bankmanagmentsystem.entity.Loan;
import com.ritesh.bankmanagmentsystem.entity.LoanStatus;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.exception.BusinessException;
import com.ritesh.bankmanagmentsystem.exception.ResourceNotFoundException;
import com.ritesh.bankmanagmentsystem.repository.AccountRepository;
import com.ritesh.bankmanagmentsystem.repository.LoanRepository;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import com.ritesh.bankmanagmentsystem.util.SecurityUtils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;
    private final AuditService auditService;

    public LoanService(
        LoanRepository loanRepository,
        UserRepository userRepository,
        AccountRepository accountRepository,
        SecurityUtils securityUtils,
        AuditService auditService
    ) {
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
        this.auditService = auditService;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF') or #request.userId == @securityUtils.getCurrentUser().id")
    public LoanResponse apply(LoanApplyRequest request, String ipAddress) {
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setAccount(getOptionalAccount(request.accountId()));
        loan.setPrincipalAmount(request.principalAmount());
        loan.setAnnualInterestRate(request.annualInterestRate());
        loan.setTenureMonths(request.tenureMonths());
        loan.setMonthlyEmi(calculateEmi(request.principalAmount(), request.annualInterestRate(), request.tenureMonths()));
        loan.setStatus(LoanStatus.PENDING);
        loan.setRemarks(request.remarks());

        Loan saved = loanRepository.save(loan);
        User actor = securityUtils.getCurrentUser();
        auditService.log(actor.getId(), "LOAN_APPLY", "LOAN", String.valueOf(saved.getId()), "Loan application submitted", ipAddress);
        return map(saved);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public LoanResponse approve(Long loanId, LoanDecisionRequest request, String ipAddress) {
        Loan loan = getPendingLoan(loanId);
        User actor = securityUtils.getCurrentUser();
        loan.setStatus(LoanStatus.APPROVED);
        loan.setRemarks(request.remarks());
        loan.setReviewedBy(actor.getId());
        loan.setReviewedAt(Instant.now());
        Loan saved = loanRepository.save(loan);
        auditService.log(actor.getId(), "LOAN_APPROVE", "LOAN", String.valueOf(saved.getId()), "Loan approved", ipAddress);
        return map(saved);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public LoanResponse reject(Long loanId, LoanDecisionRequest request, String ipAddress) {
        Loan loan = getPendingLoan(loanId);
        User actor = securityUtils.getCurrentUser();
        loan.setStatus(LoanStatus.REJECTED);
        loan.setRemarks(request.remarks());
        loan.setReviewedBy(actor.getId());
        loan.setReviewedAt(Instant.now());
        Loan saved = loanRepository.save(loan);
        auditService.log(actor.getId(), "LOAN_REJECT", "LOAN", String.valueOf(saved.getId()), "Loan rejected", ipAddress);
        return map(saved);
    }

    public List<LoanResponse> getMyLoans() {
        User user = securityUtils.getCurrentUser();
        return loanRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream().map(this::map).toList();
    }

    private Loan getPendingLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        if (loan.getStatus() != LoanStatus.PENDING) {
            throw new BusinessException("Loan is already processed");
        }
        return loan;
    }

    private Account getOptionalAccount(Long accountId) {
        if (accountId == null) {
            return null;
        }
        return accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }

    private BigDecimal calculateEmi(BigDecimal principal, BigDecimal annualRate, int months) {
        BigDecimal monthlyRate = annualRate.divide(BigDecimal.valueOf(1200), 12, RoundingMode.HALF_UP);
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(BigDecimal.valueOf(months), 2, RoundingMode.HALF_UP);
        }
        BigDecimal onePlusRPowerN = BigDecimal.ONE.add(monthlyRate).pow(months, MathContext.DECIMAL128);
        BigDecimal numerator = principal.multiply(monthlyRate).multiply(onePlusRPowerN);
        BigDecimal denominator = onePlusRPowerN.subtract(BigDecimal.ONE);
        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    private LoanResponse map(Loan loan) {
        return new LoanResponse(
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
        );
    }
}

