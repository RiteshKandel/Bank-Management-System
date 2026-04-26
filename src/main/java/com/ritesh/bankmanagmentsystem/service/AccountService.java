package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.dto.account.AccountResponse;
import com.ritesh.bankmanagmentsystem.dto.account.CreateAccountRequest;
import com.ritesh.bankmanagmentsystem.entity.Account;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.exception.ResourceNotFoundException;
import com.ritesh.bankmanagmentsystem.repository.AccountRepository;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import com.ritesh.bankmanagmentsystem.util.SecurityUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final SecurityUtils securityUtils;

    public AccountService(
        AccountRepository accountRepository,
        UserRepository userRepository,
        AuditService auditService,
        SecurityUtils securityUtils
    ) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.securityUtils = securityUtils;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public AccountResponse createAccount(CreateAccountRequest request, String ipAddress) {
        User owner = userRepository.findById(request.ownerUserId())
            .orElseThrow(() -> new ResourceNotFoundException("Owner user not found"));

        Account account = new Account();
        account.setOwner(owner);
        account.setAccountType(request.accountType());
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);

        Account saved = accountRepository.save(account);
        User actor = securityUtils.getCurrentUser();
        auditService.log(actor.getId(), "ACCOUNT_CREATE", "ACCOUNT", String.valueOf(saved.getId()), "Created account for user " + owner.getId(), ipAddress);
        return map(saved);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF') or @accountAccess.canAccess(#id)")
    public AccountResponse getAccountById(Long id) {
        Account account = accountRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return map(account);
    }

    public List<AccountResponse> getMyAccounts() {
        User current = securityUtils.getCurrentUser();
        return accountRepository.findByOwner(current).stream().map(this::map).toList();
    }

    private String generateAccountNumber() {
        return "AC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
    }

    private AccountResponse map(Account account) {
        return new AccountResponse(
            account.getId(),
            account.getAccountNumber(),
            account.getOwner().getId(),
            account.getAccountType(),
            account.getBalance(),
            account.isActive(),
            account.getCreatedAt(),
            account.getUpdatedAt()
        );
    }
}

