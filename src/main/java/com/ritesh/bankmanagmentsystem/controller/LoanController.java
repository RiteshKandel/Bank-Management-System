package com.ritesh.bankmanagmentsystem.controller;

import com.ritesh.bankmanagmentsystem.dto.loan.LoanApplyRequest;
import com.ritesh.bankmanagmentsystem.dto.loan.LoanDecisionRequest;
import com.ritesh.bankmanagmentsystem.dto.loan.LoanResponse;
import com.ritesh.bankmanagmentsystem.service.LoanService;
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
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @PostMapping("/apply")
    public LoanResponse apply(@Valid @RequestBody LoanApplyRequest request, HttpServletRequest httpRequest) {
        return loanService.apply(request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/{id}/approve")
    public LoanResponse approve(@PathVariable Long id, @RequestBody LoanDecisionRequest request, HttpServletRequest httpRequest) {
        return loanService.approve(id, request, httpRequest.getRemoteAddr());
    }

    @PostMapping("/{id}/reject")
    public LoanResponse reject(@PathVariable Long id, @RequestBody LoanDecisionRequest request, HttpServletRequest httpRequest) {
        return loanService.reject(id, request, httpRequest.getRemoteAddr());
    }

    @GetMapping("/my")
    public List<LoanResponse> myLoans() {
        return loanService.getMyLoans();
    }
}

