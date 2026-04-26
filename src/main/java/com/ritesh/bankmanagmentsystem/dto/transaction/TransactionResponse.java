package com.ritesh.bankmanagmentsystem.dto.transaction;

import com.ritesh.bankmanagmentsystem.entity.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
    Long id,
    String referenceNumber,
    TransactionType transactionType,
    BigDecimal amount,
    Long sourceAccountId,
    Long targetAccountId,
    String description,
    Long performedByUserId,
    Instant createdAt
) {
}

