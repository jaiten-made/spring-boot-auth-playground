# 5. End-to-End Testing Strategy

Date: 2026-08-15

## Status

Accepted

## Context

While unit tests verify individual components in isolation, they do not verify how the components interact under real-world request cycles. Specifically, we need to test that Spring Security filters, JSON serialization/deserialization, JWT validation headers, and the controller mapping layers work together seamlessly from request to response.

## Options Considered

1. **Browser-level UI Testing (Selenium / Playwright)**: Simulates a real user interacting with the UI. However, this is heavyweight, slow, prone to flakiness, requires downloading browser binaries, and adds significant bloat to the workspace.
2. **Real Network E2E Tests (Random Port with `TestRestTemplate`)**: Starts a real web server and sends network HTTP requests. However, this introduces network latency, port management overhead, and slower feedback loops.
3. **Mock Servlet E2E Integration Tests (`@SpringBootTest` + `@AutoConfigureMockMvc`)**: Runs the full Spring application context, security filters, controller mappings, and in-memory database, but communicates via mock HTTP requests.

## Decision

We chose **Mock Servlet E2E Integration Tests (`@SpringBootTest` + `@AutoConfigureMockMvc`)** for API-level verification. We wrote `AuthIntegrationTest.java` to simulate the full user registration -> login -> secure API request flow in one transactional test.

## Consequences

- **Pros**:
  - Tests the entire Spring Boot server and filter chain end-to-end (Request -> Filter -> Controller -> Service -> Database -> Response).
  - Highly reliable and fast (no browser or port conflicts).
  - Avoids dependency bloat and flakiness associated with Selenium/Playwright UI tests.
- **Cons**:
  - Does not test frontend UI interactions (e.g., JavaScript execution or CSS rendering on the user's browser).
  - Does not test actual socket/TCP connections over the network loopback.
