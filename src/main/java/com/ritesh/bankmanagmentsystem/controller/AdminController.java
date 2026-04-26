package com.ritesh.bankmanagmentsystem.controller;

import com.ritesh.bankmanagmentsystem.dto.admin.AuditLogResponse;
import com.ritesh.bankmanagmentsystem.dto.loan.LoanResponse;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransactionResponse;
import com.ritesh.bankmanagmentsystem.dto.user.UserResponse;
import com.ritesh.bankmanagmentsystem.service.AdminService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public List<UserResponse> users() {
        return adminService.users();
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> transactions() {
        return adminService.transactions();
    }

    @GetMapping("/loans")
    public List<LoanResponse> loans() {
        return adminService.loans();
    }

    @GetMapping("/audits")
    public List<AuditLogResponse> audits() {
        return adminService.audits();
    }
}

