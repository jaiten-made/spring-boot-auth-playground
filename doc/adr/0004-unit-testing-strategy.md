# 4. Unit Testing Strategy

Date: 2026-08-15

## Status

Accepted

## Context

To ensure the reliability, security, and correctness of core utility logic (such as JWT generation and validation) and database persistence logic, we need a robust, fast, and repeatable unit testing strategy. Unit tests must run quickly without spinning up unnecessary parts of the web stack.

## Options Considered

1. **Full Spring Boot Context Tests (`@SpringBootTest`) for everything**: Easy to configure, but extremely slow as it spins up the entire application container, database, and configurations for every test class.
2. **Isolated Unit Tests (Mockito & DB-slice testing)**:
   - Plain JUnit 5 with Mockito (`@ExtendWith(MockitoExtension.class)`) for testing isolated services and helper classes (like `JwtUtils` and `CustomUserDetailsService`) with fast mock-based verification.
   - Database Integration / Slice Testing (`@SpringBootTest` with `@Transactional` or `@DataJpaTest` on H2) for verifying database entities, custom queries, and database constraints (like unique constraints) without starting the HTTP servlet web layer.

## Decision

We chose **Isolated Unit Tests (Mockito and Database Slice Testing)**. We use:
- Plain JUnit 5 for `JwtUtilsTest` (supplying custom keys directly to constructor) and `CustomUserDetailsServiceTest` (mocking the `UserRepository` with Mockito).
- `@SpringBootTest` + `@Transactional` (using the lightweight in-memory H2 database) for `UserRepositoryTest` to test database query operations, saving entities, and validating constraints (such as unique usernames) with automatic database rollback after each test method.

## Consequences

- **Pros**:
  - Extremely fast execution (the whole unit test suite runs in under 4 seconds).
  - High confidence in isolated component logic (JWT expiry handling, password encoding logic, database unique constraints).
  - Clear distinction between isolated code tests and E2E system flows.
- **Cons**:
  - Requires mocking database behaviors when testing service classes like `CustomUserDetailsService` (does not test real DB interactions for those classes).
