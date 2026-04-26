## Plan: Production Bank Management MVP

Create a secure monorepo-style banking platform by expanding the current Spring Boot project into a clean, layered backend and adding a React frontend, PostgreSQL schema migrations, and deployment docs. The plan emphasizes JWT security, role-based access, transaction-safe money operations, and production-ready structure so the system is maintainable, scalable, and easy to extend with optional Redis/Kafka/Docker.

### Steps
1. Baseline architecture and dependencies in [build.gradle](build.gradle), [src/main/resources/application.properties](src/main/resources/application.properties), and package layout for `controller`, `service`, `repository`, `dto`, `entity`, `security`.
2. Design normalized schema and migrations in [src/main/resources/db/migration](src/main/resources/db/migration) for `users`, `roles`, `accounts`, `transactions`, `loans`, `audit_logs` with indexes and foreign keys.
3. Implement security/auth flow with `SecurityConfig`, `JwtAuthenticationFilter`, `JwtService`, `AuthController`, and BCrypt-backed registration/login endpoints.
4. Build banking domain APIs via `UserService`, `AccountService`, `TransactionService`, `LoanService`, `AdminService`, enforcing ACID transfers, locking/idempotency, validation, and audit logging.
5. Add frontend app in [frontend](frontend) with `pages`, `components`, `services`, `context`, `hooks`, protected routes, JWT token lifecycle, and role-aware dashboards.
6. Finalize production assets: OpenAPI/Swagger config, seed scripts, Docker setup, and operational README in [README.md](README.md) with environment and startup guidance.

### Further Considerations
1. Confirm runtime baseline: stay on Spring Boot `4.0.6` or switch to Spring Boot 3.x LTS for broader production compatibility?
2. Confirm API contract style: strict versioned paths (`/api/v1/...`) now or unversioned MVP endpoints with later versioning?
3. Review this draft plan and choose scope: A) Core modules only, B) Core + Loan/Admin, C) Full scope + Redis/Kafka + Docker.

