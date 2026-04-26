package com.ritesh.bankmanagmentsystem.security;

import com.ritesh.bankmanagmentsystem.entity.Account;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.repository.AccountRepository;
import com.ritesh.bankmanagmentsystem.util.SecurityUtils;
import org.springframework.stereotype.Component;

@Component("accountAccess")
public class AccountAccessEvaluator {

    private final AccountRepository accountRepository;
    private final SecurityUtils securityUtils;

    public AccountAccessEvaluator(AccountRepository accountRepository, SecurityUtils securityUtils) {
        this.accountRepository = accountRepository;
        this.securityUtils = securityUtils;
    }

    public boolean canAccess(Long accountId) {
        User current = securityUtils.getCurrentUser();
        Account account = accountRepository.findById(accountId).orElse(null);
        return account != null && account.getOwner().getId().equals(current.getId());
    }
}

