package com.ritesh.bankmanagmentsystem.dto.loan;

import com.ritesh.bankmanagmentsystem.entity.LoanStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record LoanResponse(
    Long id,
    Long userId,
    Long accountId,
    BigDecimal principalAmount,
    BigDecimal annualInterestRate,
    Integer tenureMonths,
    BigDecimal monthlyEmi,
    LoanStatus status,
    String remarks,
    Long reviewedBy,
    Instant reviewedAt,
    Instant createdAt
) {
}

