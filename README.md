
# Resilience4j Testing Lab

A practical Java project demonstrating automated testing of resilience mechanisms using Resilience4j, JUnit 5 and Mockito.

## Overview

This repository demonstrates how to implement and test fault-tolerance mechanisms in Java applications.

The project focuses on two important resilience patterns:

- Retry: automatically retries failed operations.
- Circuit Breaker: prevents repeated calls to an external service when failures reach a configured threshold.

## Technologies

- Java 21 (LTS)
- Maven
- Resilience4j 2.3.0
- JUnit 5
- Mockito 5

## Project Structure

```text
resilience4j-testing-lab/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── demo/
    └── test/
        └── java/
            └── demo/
```

## Implemented Tests

### 1. OrderServiceTest

Tests the Retry mechanism.

The test simulates a temporary failure in an operation and verifies that the service retries the call until it succeeds.

Topics demonstrated:

- Retry configuration
- Handling temporary failures
- Mockito stubbing
- JUnit assertions

### 2. PaymentProviderAdapterTest

Tests the Circuit Breaker mechanism.

The test simulates failures from an external payment provider and verifies that the circuit opens after reaching the configured failure threshold.

Topics demonstrated:

- Circuit Breaker configuration
- CLOSED and OPEN states
- Failure rate threshold
- CallNotPermittedException
- Verification of external service invocations

## Running the Tests

Run all tests:

```bash
mvn clean test
```

Run only the Retry test:

```bash
mvn -Dtest=OrderServiceTest test
```

Run only the Circuit Breaker test:

```bash
mvn -Dtest=PaymentProviderAdapterTest test
```

Run a specific test method:

```bash
mvn "-Dtest=PaymentProviderAdapterTest#shouldOpenCircuitAfterTwoFailures" test
```

## Learning Objectives

This project explores practical techniques for:

- Testing fault-tolerant Java services.
- Simulating external service failures.
- Validating retry policies.
- Testing circuit breaker state transitions.
- Using Mockito to isolate dependencies.
- Verifying exception handling with JUnit 5.

## Author

Java Developer | Backend Engineering | Spring Boot | Microservices

GitHub: https://github.com/icemelid

