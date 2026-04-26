package com.ritesh.bankmanagmentsystem;

import com.ritesh.bankmanagmentsystem.dto.auth.AuthResponse;
import com.ritesh.bankmanagmentsystem.dto.auth.LoginRequest;
import com.ritesh.bankmanagmentsystem.dto.auth.RegisterRequest;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import com.ritesh.bankmanagmentsystem.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthFlowIntegrationTests {

    @Autowired
    private AuthService authService;


    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanup() {
        userRepository.findByEmail("customer1@bank.local").ifPresent(userRepository::delete);
    }

    @Test
    void registerThenLoginShouldReturnJwtToken() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Customer One", "customer1@bank.local", "Customer@123");
        AuthResponse registerResponse = authService.register(registerRequest);
        assertThat(registerResponse.token()).isNotBlank();
        assertThat(registerResponse.email()).isEqualTo("customer1@bank.local");

        LoginRequest loginRequest = new LoginRequest("customer1@bank.local", "Customer@123");
        AuthResponse loginResponse = authService.login(loginRequest);

        assertThat(loginResponse.token()).isNotBlank();
        assertThat(loginResponse.tokenType()).isEqualTo("Bearer");
    }
}

