# 2. Use Spring Security OAuth2 Resource Server for JWT Authentication

Date: 2026-08-15

## Status

Accepted

## Context

We originally had a custom `JwtAuthenticationFilter` extending `OncePerRequestFilter` to perform manual HTTP header parsing, token extraction, signature verification, and security context mapping using the `jjwt` library. We wanted to adopt a more built-in, standard, and robust way to process JWT authentication within Spring Boot.

## Options Considered

1. **Custom `OncePerRequestFilter` with manual decoding**: High control, but requires writing, testing, and maintaining boilerplate code for parsing the Authorization header, extracting the token, checking validation, and populating the security context.
2. **Spring Security OAuth2 Resource Server**: An official, built-in mechanism that leverages standard filters and Nimbus Jose-JWT under the hood. It fully automates the JWT extraction and validation lifecycle out-of-the-box.

## Decision

We migrated to **Spring Security OAuth2 Resource Server** by adding the `spring-boot-starter-oauth2-resource-server` dependency and configuring the built-in JWT support in `SecurityConfig.java`.

## Consequences

- **Pros**:
  - Eliminated the custom `JwtAuthenticationFilter` boilerplate file entirely.
  - Rely on standard, tested, and secure token validation logic maintained by Spring Security.
  - The configuration in `SecurityConfig.java` becomes simpler and cleaner.
- **Cons**:
  - The security principal in the security context changes from `UserDetails` to a native `Jwt` token.
  - Mocks in controller web-slice unit tests (`AuthControllerTest` and `UserControllerTest`) had to be modified to remove the custom filter stubbing.
