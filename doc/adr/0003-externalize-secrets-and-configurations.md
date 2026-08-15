# 3. Externalize Secrets and Configurations

Date: 2026-08-15

## Status

Accepted

## Context

The JWT secret key was previously hardcoded directly into the Java source code in both `SecurityConfig.java` and `JwtUtils.java`. Hardcoding secrets is a major security vulnerability as they get checked into version control. We needed a secure way to manage this cryptographic key while keeping setup simple and zero-friction for local development.

## Options Considered

1. **Third-party Dotenv Library (e.g., `spring-dotenv`)**: Automatically loads variables from a `.env` file, but adds extra library dependencies and can complicate test setups.
2. **Spring Property Placeholders with Env Bindings**: Define the property in `application.properties` with a placeholder that binds to a system environment variable (`JWT_SECRET`) and falls back to a development key (`app.jwt.secret=${JWT_SECRET:dev_key}`). This is native to Spring Boot and requires no extra dependencies.

## Decision

We chose **Spring Property Placeholders with Env Bindings**. We externalized the secret configuration into `application.properties` and injected it into Java classes using Spring's `@Value` annotation with constructor injection where appropriate. We also created a local `.env` and `.env.example` file structure for developers, adding `.env` to `.gitignore`.

## Consequences

- **Pros**:
  - Code is safe from credentials leakage; hardcoded secrets are removed.
  - Development startup remains zero-friction because the app falls back to a default key if the environment variable is missing.
  - Production deployments can easily inject the secret via native OS environment variables or platform configuration managers (Docker/Kubernetes).
  - Clean unit test setup: we can inject custom test keys directly into the `JwtUtils` constructor without loading the Spring context or properties files.
- **Cons**:
  - Developers need to configure their IDE to load the `.env` file if they want to override the default key locally during active debugging.
