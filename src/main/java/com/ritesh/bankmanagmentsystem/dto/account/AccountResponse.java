package com.ritesh.bankmanagmentsystem.dto.account;

import com.ritesh.bankmanagmentsystem.entity.AccountType;
import java.math.BigDecimal;
import java.time.Instant;

public record AccountResponse(
    Long id,
    String accountNumber,
    Long ownerUserId,
    AccountType accountType,
    BigDecimal balance,
    boolean active,
    Instant createdAt,
    Instant updatedAt
) {
}

