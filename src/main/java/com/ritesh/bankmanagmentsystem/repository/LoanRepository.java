package com.ritesh.bankmanagmentsystem.repository;

import com.ritesh.bankmanagmentsystem.entity.Loan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Loan> findAllByOrderByCreatedAtDesc();
}

