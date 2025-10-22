# Testing Guide for LibraryHub Backend

## Overview
This project includes comprehensive unit tests, integration tests, and code coverage reporting using JaCoCo.

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

## Running Tests

### Run All Tests
```bash
cd libraryhub
./mvnw clean test
```

### Run Tests with Coverage Report
```bash
./mvnw clean test jacoco:report
```

### Run Tests and Check Coverage Threshold (85%)
```bash
./mvnw clean verify
```

## Viewing Coverage Reports

After running tests with `jacoco:report`, you can view the coverage report:

1. Open the file: `target/site/jacoco/index.html` in your browser
2. The report shows line coverage, branch coverage, and method coverage
3. You can drill down into packages, classes, and individual methods

### Coverage Metrics
- **Line Coverage**: Percentage of code lines executed during tests
- **Branch Coverage**: Percentage of decision branches (if/else) tested
- **Method Coverage**: Percentage of methods invoked during tests

## Test Structure

### Model Tests (`src/test/java/.../model/`)
- `BookTest.java` - Tests for Book entity
- `UserTest.java` - Tests for User and Customer entities
- `ReviewTest.java` - Tests for Review entity
- `ReservationTest.java` - Tests for Reservation entity

### Service Tests (`src/test/java/.../service/`)
- `BookServiceTest.java` - Tests for book management
- `AuthServiceTest.java` - Tests for authentication
- `ReservationServiceTest.java` - Tests for reservation logic
- `ReviewServiceTest.java` - Tests for review operations
- `WaitingListServiceTest.java` - Tests for waiting list functionality
- `ProfileServiceTest.java` - Tests for user profile management

### Controller Tests (`src/test/java/.../controller/`)
- `BookRestControllerTest.java` - Tests for book REST endpoints
- `ReservationRestControllerTest.java` - Tests for reservation endpoints

### Repository Tests (`src/test/java/.../repository/`)
- `BookRepositoryTest.java` - Tests for book repository
- `UserRepositoryTest.java` - Tests for user repository

### Integration Tests (`src/test/java/.../integration/`)
- `BookReservationIntegrationTest.java` - End-to-end flow testing

### DTO Tests (`src/test/java/.../dto/`)
- `BookDTOTest.java` - Tests for BookDTO
- `RegisterRequestTest.java` - Tests for RegisterRequest

## Test Coverage Goals

The project is configured to enforce **85% code coverage** minimum. The build will fail if coverage falls below this threshold.

### Current Coverage Target
- **Minimum Line Coverage**: 85%
- Applied at the package level

## Testing Best Practices

1. **Isolation**: Each test is isolated using mocks (Mockito)
2. **Arrange-Act-Assert**: Tests follow the AAA pattern
3. **Descriptive Names**: Test method names describe what they test
4. **Edge Cases**: Tests cover both happy paths and error scenarios
5. **Database**: Integration tests use H2 in-memory database

## Technologies Used

- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework
- **Spring Boot Test**: Spring testing support
- **H2 Database**: In-memory database for tests
- **JaCoCo**: Code coverage tool
- **MockMvc**: For testing REST controllers

## Common Test Commands

```bash
# Run only unit tests
./mvnw test

# Run tests in a specific package
./mvnw test -Dtest="com.example.libraryhub.service.*Test"

# Run a specific test class
./mvnw test -Dtest=BookServiceTest

# Run a specific test method
./mvnw test -Dtest=BookServiceTest#testGetAllBooks

# Skip tests
./mvnw clean install -DskipTests

# Run tests with detailed output
./mvnw test -X
```

## Continuous Integration

These tests are designed to run in CI/CD pipelines. The coverage report can be published as an artifact or integrated with code quality tools like SonarQube.

## Troubleshooting

### Tests fail due to missing dependencies
```bash
./mvnw clean install
```

### H2 database issues
Check `src/test/resources/application-test.properties` for correct H2 configuration.

### Coverage not generated
Ensure you run `./mvnw clean test` (not just `mvn test`) to trigger JaCoCo agent.

### Port conflicts in integration tests
Make sure no other services are using the same ports during test execution.

## Adding New Tests

When adding new features:
1. Write unit tests for services
2. Write controller tests for endpoints
3. Write integration tests for complex flows
4. Ensure coverage stays above 85%
5. Run `./mvnw verify` before committing

## Test Data

Test data is created using:
- `@BeforeEach` methods for setup
- Builder patterns for complex objects
- Factory methods in test classes
- H2 in-memory database for integration tests
