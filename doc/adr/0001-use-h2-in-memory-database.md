# 1. Use H2 In-Memory Database for Playground

Date: 2026-08-15

## Status

Accepted

## Context

This project is a playground for exploring Spring Boot authentication (JWT, Security setup). We need a database to store user credentials, but we want to minimize setup friction and ensure the application can be spun up as fast and easily as possible.

## Options Considered

1. **Dockerized Database (e.g., PostgreSQL / MySQL via Docker Compose)**: Requires Docker to be installed and running.
2. **Manual Local Database**: Requires manual installation, setup, and background service configuration on the developer's system.
3. **H2 In-Memory Database**: Zero installation or configuration required. Runs entirely within the JVM process.

## Decision

We chose the **H2 In-Memory Database** because this is a simple auth playground. The primary goal is a fast and zero-friction spin up.

## Consequences

- **Pros**:
  - No external dependencies (no Docker or local DB engine required).
  - Extremely fast startup.
  - Test suites run instantly in memory.
- **Cons**:
  - Data does not persist across application restarts.
