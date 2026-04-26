package com.ritesh.bankmanagmentsystem.controller;

import com.ritesh.bankmanagmentsystem.dto.account.AccountResponse;
import com.ritesh.bankmanagmentsystem.dto.account.CreateAccountRequest;
import com.ritesh.bankmanagmentsystem.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request, HttpServletRequest httpRequest) {
        return accountService.createAccount(request, httpRequest.getRemoteAddr());
    }

    @GetMapping("/{id}")
    public AccountResponse getById(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/my")
    public List<AccountResponse> myAccounts() {
        return accountService.getMyAccounts();
    }
}

