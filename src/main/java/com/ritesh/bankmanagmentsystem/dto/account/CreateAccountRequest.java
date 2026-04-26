package com.ritesh.bankmanagmentsystem.dto.account;

import com.ritesh.bankmanagmentsystem.entity.AccountType;
import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(
    @NotNull Long ownerUserId,
    @NotNull AccountType accountType
) {
}

