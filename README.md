# Spring Boot Auth Playground

A playground project demonstrating JWT authentication and role-based access control with Spring Boot Security.

## Features

- **JWT Auth**: Bearer token generation and validation using custom filter.
- **H2 Database**: In-memory database with H2 Console enabled.
- **Swagger/OpenAPI**: Interactive UI sandbox to test endpoints.
  - Swagger UI: `http://localhost:8080/swagger-ui/index.html`
  - OpenAPI Docs: `http://localhost:8080/v3/api-docs`
- **REST Endpoints**:
  - `POST /api/v1/users` - Create a new user.
  - `POST /api/v1/auth/login` - Authenticate credentials and get JWT token.
  - `GET /api/v1/auth/public-data` - Public endpoint.
  - `GET /api/v1/auth/private-data` - Secured endpoint requiring valid JWT.

## Setup & Running

Requires Java 17+.

```bash
# Run tests
./gradlew test

# Start the application
./gradlew bootRun
```
