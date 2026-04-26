package com.ritesh.bankmanagmentsystem.controller;

import com.ritesh.bankmanagmentsystem.dto.transaction.AmountOperationRequest;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransactionResponse;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransferRequest;
import com.ritesh.bankmanagmentsystem.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public TransactionResponse deposit(@Valid @RequestBody AmountOperationRequest request, HttpServletRequest httpRequest) {
        return transactionService.deposit(request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/withdraw")
    public TransactionResponse withdraw(@Valid @RequestBody AmountOperationRequest request, HttpServletRequest httpRequest) {
        return transactionService.withdraw(request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request, HttpServletRequest httpRequest) {
        return transactionService.transfer(request, httpRequest.getRemoteAddr());
    }

    @GetMapping("/history/{accountId}")
    public List<TransactionResponse> history(@PathVariable Long accountId) {
        return transactionService.getHistory(accountId);
    }
}

