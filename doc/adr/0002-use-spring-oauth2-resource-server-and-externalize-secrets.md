# 2. Use Spring Security OAuth2 Resource Server and Externalize Secrets

Date: 2026-08-15

## Status

Accepted

## Context

We originally had a custom `JwtAuthenticationFilter` extending `OncePerRequestFilter` using manual JWT decoding via the `jjwt` library. Additionally, the JWT secret key was hardcoded in both `SecurityConfig.java` and `JwtUtils.java`. 

We wanted:
1. A more robust, standard, and built-in way to handle JWT validation in Spring Boot.
2. A secure configuration strategy to avoid hardcoding secrets in version control, while maintaining a low-friction startup for local developers.

## Options Considered

### JWT Filtering:
1. **Custom `OncePerRequestFilter` with manual decoding**: High control, but requires writing and maintaining boilerplate extraction and validation filter code.
2. **Spring Security OAuth2 Resource Server**: Uses Spring's native built-in filter and Nimbus library under the hood. Eliminates custom filters and configuration overhead.

### Secret Management:
1. **Third-party Dotenv Library (e.g., `spring-dotenv`)**: Automatically reads a `.env` file, but adds extra library dependencies and can complicate test setups.
2. **Spring Property Fallbacks (Recommended)**: Define property mapping in `application.properties` (`app.jwt.secret=${JWT_SECRET:dev_key}`) and inject via `@Value`. Zero dependencies, clean test setup, and cloud-ready.

## Decision

We chose **Spring Security OAuth2 Resource Server** to handle token validation, and **Spring Property Fallbacks** combined with IDE environment configurations to manage the JWT secret.

## Consequences

- **Pros**:
  - Deleted custom `JwtAuthenticationFilter` boilerplate code.
  - Standard, robust cryptographic validation handled by Spring Security and the Nimbus library.
  - Hardcoded secrets removed; the application now adheres to Twelve-Factor app principles.
  - Development startup remains zero-friction due to property fallbacks.
  - Added `.env` to `.gitignore` to prevent secret leaks, providing `.env.example` as a template.
- **Cons**:
  - The security principal in the context changes from `UserDetails` to a native `Jwt` token.
  - Unit tests require updating to handle the new `JwtDecoder` bean and constructor injection of the secret.
