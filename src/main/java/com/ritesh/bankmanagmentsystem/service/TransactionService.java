package com.ritesh.bankmanagmentsystem.service;

import com.ritesh.bankmanagmentsystem.dto.transaction.AmountOperationRequest;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransactionResponse;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransferRequest;
import com.ritesh.bankmanagmentsystem.entity.Account;
import com.ritesh.bankmanagmentsystem.entity.BankTransaction;
import com.ritesh.bankmanagmentsystem.entity.TransactionType;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.exception.BusinessException;
import com.ritesh.bankmanagmentsystem.exception.ResourceNotFoundException;
import com.ritesh.bankmanagmentsystem.repository.AccountRepository;
import com.ritesh.bankmanagmentsystem.repository.BankTransactionRepository;
import com.ritesh.bankmanagmentsystem.util.SecurityUtils;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final BankTransactionRepository bankTransactionRepository;
    private final AuditService auditService;
    private final SecurityUtils securityUtils;

    public TransactionService(
        AccountRepository accountRepository,
        BankTransactionRepository bankTransactionRepository,
        AuditService auditService,
        SecurityUtils securityUtils
    ) {
        this.accountRepository = accountRepository;
        this.bankTransactionRepository = bankTransactionRepository;
        this.auditService = auditService;
        this.securityUtils = securityUtils;
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF') or @accountAccess.canAccess(#request.accountId)")
    public TransactionResponse deposit(AmountOperationRequest request, String ipAddress) {
        Account account = getLockedAccount(request.accountId());
        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);

        BankTransaction tx = createTx(TransactionType.DEPOSIT, request.amount(), null, account, request.description());
        User actor = securityUtils.getCurrentUser();
        auditService.log(actor.getId(), "DEPOSIT", "ACCOUNT", String.valueOf(account.getId()), "Deposited " + request.amount(), ipAddress);
        return map(tx);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF') or @accountAccess.canAccess(#request.accountId)")
    public TransactionResponse withdraw(AmountOperationRequest request, String ipAddress) {
        Account account = getLockedAccount(request.accountId());
        ensureSufficientBalance(account, request.amount());
        account.setBalance(account.getBalance().subtract(request.amount()));
        accountRepository.save(account);

        BankTransaction tx = createTx(TransactionType.WITHDRAWAL, request.amount(), account, null, request.description());
        User actor = securityUtils.getCurrentUser();
        auditService.log(actor.getId(), "WITHDRAW", "ACCOUNT", String.valueOf(account.getId()), "Withdrew " + request.amount(), ipAddress);
        return map(tx);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','STAFF') or @accountAccess.canAccess(#request.fromAccountId)")
    public TransactionResponse transfer(TransferRequest request, String ipAddress) {
        if (request.fromAccountId().equals(request.toAccountId())) {
            throw new BusinessException("Source and destination account cannot be same");
        }

        Long first = Math.min(request.fromAccountId(), request.toAccountId());
        Long second = Math.max(request.fromAccountId(), request.toAccountId());

        Account lockA = getLockedAccount(first);
        Account lockB = getLockedAccount(second);

        Account from = lockA.getId().equals(request.fromAccountId()) ? lockA : lockB;
        Account to = lockA.getId().equals(request.toAccountId()) ? lockA : lockB;

        ensureSufficientBalance(from, request.amount());
        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));
        accountRepository.save(from);
        accountRepository.save(to);

        BankTransaction tx = createTx(TransactionType.TRANSFER, request.amount(), from, to, request.description());
        User actor = securityUtils.getCurrentUser();
        auditService.log(actor.getId(), "TRANSFER", "ACCOUNT", from.getId() + "->" + to.getId(), "Transferred " + request.amount(), ipAddress);
        return map(tx);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF') or @accountAccess.canAccess(#accountId)")
    public List<TransactionResponse> getHistory(Long accountId) {
        return bankTransactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(accountId, accountId)
            .stream().map(this::map).toList();
    }

    private Account getLockedAccount(Long accountId) {
        return accountRepository.findByIdForUpdate(accountId)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountId));
    }

    private void ensureSufficientBalance(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient account balance");
        }
    }

    private BankTransaction createTx(TransactionType type, BigDecimal amount, Account source, Account target, String description) {
        User actor = securityUtils.getCurrentUser();
        BankTransaction tx = new BankTransaction();
        tx.setReferenceNumber("TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setTransactionType(type);
        tx.setAmount(amount);
        tx.setSourceAccount(source);
        tx.setTargetAccount(target);
        tx.setDescription(description);
        tx.setPerformedByUserId(actor.getId());
        return bankTransactionRepository.save(tx);
    }

    private TransactionResponse map(BankTransaction tx) {
        return new TransactionResponse(
            tx.getId(),
            tx.getReferenceNumber(),
            tx.getTransactionType(),
            tx.getAmount(),
            tx.getSourceAccount() == null ? null : tx.getSourceAccount().getId(),
            tx.getTargetAccount() == null ? null : tx.getTargetAccount().getId(),
            tx.getDescription(),
            tx.getPerformedByUserId(),
            tx.getCreatedAt()
        );
    }
}

