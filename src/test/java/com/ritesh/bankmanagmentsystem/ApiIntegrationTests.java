package com.ritesh.bankmanagmentsystem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ritesh.bankmanagmentsystem.dto.account.AccountResponse;
import com.ritesh.bankmanagmentsystem.dto.account.CreateAccountRequest;
import com.ritesh.bankmanagmentsystem.dto.auth.AuthResponse;
import com.ritesh.bankmanagmentsystem.dto.auth.LoginRequest;
import com.ritesh.bankmanagmentsystem.dto.auth.RegisterRequest;
import com.ritesh.bankmanagmentsystem.dto.transaction.AmountOperationRequest;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransactionResponse;
import com.ritesh.bankmanagmentsystem.dto.transaction.TransferRequest;
import com.ritesh.bankmanagmentsystem.entity.AccountType;
import com.ritesh.bankmanagmentsystem.repository.AccountRepository;
import com.ritesh.bankmanagmentsystem.repository.BankTransactionRepository;
import com.ritesh.bankmanagmentsystem.repository.UserRepository;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiIntegrationTests {

    @LocalServerPort
    private int port;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BankTransactionRepository bankTransactionRepository;

    @Autowired
    private UserRepository userRepository;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeEach
    void cleanup() {
        bankTransactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.findByEmail("api-customer1@bank.local").ifPresent(userRepository::delete);
        userRepository.findByEmail("api-customer2@bank.local").ifPresent(userRepository::delete);
        userRepository.findByEmail("api-customer3@bank.local").ifPresent(userRepository::delete);
    }

    @Test
    void authRegisterAndLoginShouldReturnJwt() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("Api Customer One", "api-customer1@bank.local", "Customer@123");

        HttpResponse<String> registerHttp = postJson("/api/auth/register", registerRequest, null);
        AuthResponse registerResponse = objectMapper.readValue(registerHttp.body(), AuthResponse.class);

        assertThat(registerHttp.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(registerResponse.token()).isNotBlank();

        HttpResponse<String> loginHttp = postJson("/api/auth/login", new LoginRequest("api-customer1@bank.local", "Customer@123"), null);
        AuthResponse loginResponse = objectMapper.readValue(loginHttp.body(), AuthResponse.class);

        assertThat(loginHttp.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(loginResponse.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void accountCreateShouldRejectCustomerAndAllowAdmin() throws Exception {
        HttpResponse<String> registerHttp = postJson(
            "/api/auth/register",
            new RegisterRequest("Api Customer Two", "api-customer2@bank.local", "Customer@123"),
            null
        );
        AuthResponse registerResponse = objectMapper.readValue(registerHttp.body(), AuthResponse.class);

        assertThat(registerResponse).isNotNull();
        Long customerUserId = registerResponse.userId();

        String customerToken = login("api-customer2@bank.local", "Customer@123");
        CreateAccountRequest createRequest = new CreateAccountRequest(customerUserId, AccountType.SAVINGS);

        HttpResponse<String> forbiddenResponse = postJson("/api/accounts/create", createRequest, customerToken);

        assertThat(forbiddenResponse.statusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());

        String adminToken = login("admin@bank.local", "Admin@123");
        HttpResponse<String> createdResponse = postJson("/api/accounts/create", createRequest, adminToken);
        AccountResponse createdBody = objectMapper.readValue(createdResponse.body(), AccountResponse.class);

        assertThat(createdResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(createdBody.accountNumber()).isNotBlank();
    }

    @Test
    void transactionApisShouldProcessAndExposeHistory() throws Exception {
        AuthResponse c1 = objectMapper.readValue(
            postJson("/api/auth/register", new RegisterRequest("Api Customer A", "api-customer1@bank.local", "Customer@123"), null).body(),
            AuthResponse.class
        );
        AuthResponse c2 = objectMapper.readValue(
            postJson("/api/auth/register", new RegisterRequest("Api Customer B", "api-customer3@bank.local", "Customer@123"), null).body(),
            AuthResponse.class
        );

        assertThat(c1).isNotNull();
        assertThat(c2).isNotNull();

        String adminToken = login("admin@bank.local", "Admin@123");

        AccountResponse a1 = objectMapper.readValue(
            postJson("/api/accounts/create", new CreateAccountRequest(c1.userId(), AccountType.SAVINGS), adminToken).body(),
            AccountResponse.class
        );
        AccountResponse a2 = objectMapper.readValue(
            postJson("/api/accounts/create", new CreateAccountRequest(c2.userId(), AccountType.SAVINGS), adminToken).body(),
            AccountResponse.class
        );

        assertThat(a1).isNotNull();
        assertThat(a2).isNotNull();

        HttpResponse<String> deposit = postJson(
            "/api/transactions/deposit",
            new AmountOperationRequest(a1.id(), new BigDecimal("300.00"), "seed"),
            adminToken
        );
        assertThat(deposit.statusCode()).isEqualTo(HttpStatus.OK.value());

        HttpResponse<String> transfer = postJson(
            "/api/transactions/transfer",
            new TransferRequest(a1.id(), a2.id(), new BigDecimal("120.00"), "move"),
            adminToken
        );
        assertThat(transfer.statusCode()).isEqualTo(HttpStatus.OK.value());

        HttpResponse<String> history = getWithAuth(
            "/api/transactions/history/" + a1.id(),
            adminToken
        );
        List<TransactionResponse> historyBody = objectMapper.readValue(history.body(), new TypeReference<List<TransactionResponse>>() { });

        assertThat(history.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(historyBody.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void protectedEndpointWithoutTokenShouldBeRejected() throws Exception {
        HttpResponse<String> response = getWithoutAuth("/api/accounts/my");
        assertThat(response.statusCode()).isIn(HttpStatus.UNAUTHORIZED.value(), HttpStatus.FORBIDDEN.value());
    }

    private String login(String email, String password) throws Exception {
        HttpResponse<String> response = postJson("/api/auth/login", new LoginRequest(email, password), null);
        AuthResponse authResponse = objectMapper.readValue(response.body(), AuthResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        return authResponse.token();
    }

    private HttpResponse<String> postJson(String path, Object body, String bearerToken) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl(path)))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));

        if (bearerToken != null) {
            builder.header("Authorization", "Bearer " + bearerToken);
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getWithAuth(String path, String bearerToken) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl(path)))
            .header("Authorization", "Bearer " + bearerToken)
            .GET()
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> getWithoutAuth(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl(path)))
            .GET()
            .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String baseUrl(String path) {
        return "http://localhost:" + port + path;
    }
}

