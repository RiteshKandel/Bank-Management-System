package com.ritesh.bankmanagmentsystem;

import com.ritesh.bankmanagmentsystem.dto.transaction.AmountOperationRequest;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransactionResponse;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransferRequest;
import com.ritesh.bankmanagmentsystem.entity.Account;
import com.ritesh.bankmanagmentsystem.entity.AccountType;
import com.ritesh.bankmanagmentsystem.entity.User;
import com.ritesh.bankmanagmentsystem.exception.BusinessException;
import com.ritesh.bankmanagmentsystem.repository.AccountRepository;
import com.ritesh.bankmanagmentsystem.repository.BankTransactionRepository;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import com.ritesh.bankmanagmentsystem.service.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class TransactionServiceIntegrationTests {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @BeforeEach
    void cleanup() {
        bankTransactionRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin@bank.local", roles = {"ADMIN", "STAFF"})
    void depositWithdrawAndTransferShouldUpdateBalancesAndPersistTransactions() {
        User admin = userRepository.findByEmail("admin@bank.local").orElseThrow();
        Account accountA = createAccount(admin, new BigDecimal("1000.00"));
        Account accountB = createAccount(admin, new BigDecimal("500.00"));

        TransactionResponse deposit = transactionService.deposit(
            new AmountOperationRequest(accountA.getId(), new BigDecimal("200.00"), "cash deposit"),
            "127.0.0.1"
        );
        assertThat(deposit.referenceNumber()).startsWith("TX-");
        assertThat(deposit.transactionType().name()).isEqualTo("DEPOSIT");

        TransactionResponse withdraw = transactionService.withdraw(
            new AmountOperationRequest(accountA.getId(), new BigDecimal("100.00"), "atm withdrawal"),
            "127.0.0.1"
        );
        assertThat(withdraw.transactionType().name()).isEqualTo("WITHDRAWAL");

        TransactionResponse transfer = transactionService.transfer(
            new TransferRequest(accountA.getId(), accountB.getId(), new BigDecimal("250.00"), "rent transfer"),
            "127.0.0.1"
        );
        assertThat(transfer.transactionType().name()).isEqualTo("TRANSFER");

        Account refreshedA = accountRepository.findById(accountA.getId()).orElseThrow();
        Account refreshedB = accountRepository.findById(accountB.getId()).orElseThrow();

        assertThat(refreshedA.getBalance()).isEqualByComparingTo(new BigDecimal("850.00"));
        assertThat(refreshedB.getBalance()).isEqualByComparingTo(new BigDecimal("750.00"));
        assertThat(bankTransactionRepository.count()).isEqualTo(3);
    }

    @Test
    @WithMockUser(username = "admin@bank.local", roles = {"ADMIN", "STAFF"})
    void withdrawShouldFailWhenBalanceIsInsufficient() {
        User admin = userRepository.findByEmail("admin@bank.local").orElseThrow();
        Account account = createAccount(admin, new BigDecimal("50.00"));

        assertThatThrownBy(() -> transactionService.withdraw(
            new AmountOperationRequest(account.getId(), new BigDecimal("60.00"), "overdraw attempt"),
            "127.0.0.1"
        )).isInstanceOf(BusinessException.class)
            .hasMessageContaining("Insufficient account balance");

        Account refreshed = accountRepository.findById(account.getId()).orElseThrow();
        assertThat(refreshed.getBalance()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(bankTransactionRepository.count()).isZero();
    }

    private Account createAccount(User owner, BigDecimal openingBalance) {
        Account account = new Account();
        account.setOwner(owner);
        account.setAccountType(AccountType.SAVINGS);
        account.setAccountNumber("AC" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase());
        account.setBalance(openingBalance);
        return accountRepository.save(account);
    }
}

