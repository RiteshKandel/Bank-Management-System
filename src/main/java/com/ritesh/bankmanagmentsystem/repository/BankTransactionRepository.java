package com.ritesh.bankmanagmentsystem.repository;

import com.ritesh.bankmanagmentsystem.entity.BankTransaction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankTransactionRepository extends JpaRepository<BankTransaction, Long> {
    List<BankTransaction> findBySourceAccountIdOrTargetAccountIdOrderByCreatedAtDesc(Long sourceAccountId, Long targetAccountId);
}

