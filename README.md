# Spring Boot Auth Playground

A playground project demonstrating JWT authentication and role-based access control with Spring Boot Security.

## Features

- **JWT Auth**: Bearer token generation and validation using custom filter.
- **H2 Database**: In-memory database with H2 Console enabled.
- **REST Endpoints**:
  - `POST /api/auth/register` - Create a new user.
  - `POST /api/auth/login` - Authenticate credentials and get JWT token.
  - `GET /api/auth/public` - Public endpoint.
  - `GET /api/auth/private` - Secured endpoint requiring valid JWT.

## Setup & Running

Requires Java 17+.

```bash
# Run tests
./gradlew test

# Start the application
./gradlew bootRun
```
