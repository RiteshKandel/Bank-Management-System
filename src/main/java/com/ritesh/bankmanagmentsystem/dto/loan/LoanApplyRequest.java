package com.ritesh.bankmanagmentsystem.dto.loan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record LoanApplyRequest(
    @NotNull Long userId,
    Long accountId,
    @NotNull @DecimalMin("1000.00") BigDecimal principalAmount,
    @NotNull @DecimalMin("1.00") BigDecimal annualInterestRate,
    @NotNull @Min(6) @Max(360) Integer tenureMonths,
    String remarks
) {
}

