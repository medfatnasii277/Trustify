# Policy Service

A microservice for managing insurance policies, integrated with Keycloak for authentication and authorization.

## Overview

The Policy Service is a Spring Boot-based microservice that provides functionality for managing various types of insurance policies, including:

- Life Insurance Policies
- Car Insurance Policies
- House Insurance Policies

It follows a layered architecture pattern with clear separation of concerns between components.

## Technologies

- Java 21
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Security with OAuth2 Resource Server
- H2 Database (for development)
- Lombok
- MapStruct
- Springdoc OpenAPI

## Architecture

The service follows a layered enterprise architecture:

1. **Model Layer**: Domain entities representing the core business objects
2. **DTO Layer**: Data Transfer Objects for API requests and responses
3. **Repository Layer**: Data access interfaces using Spring Data JPA
4. **Service Layer**: Business logic and transaction management
5. **Controller Layer**: REST API endpoints
6. **Exception Handling**: Global exception handling for consistent error responses

## Authentication & Authorization

The service is configured as an OAuth2 Resource Server that validates access tokens issued by Keycloak. The security configuration includes:

- JWT token validation
- Role-based access control
- Resource ownership validation

## API Endpoints

The following API endpoints are available:

### Public Endpoints

- `GET /api/public/health`: Health check endpoint

### User Profile Endpoints

- `POST /api/profiles`: Create a new user profile
- `PUT /api/profiles/{id}`: Update an existing user profile
- `GET /api/profiles/{id}`: Get a user profile by ID
- `GET /api/profiles/me`: Get the current user's profile
- `GET /api/profiles`: Get all user profiles (admin only)
- `DELETE /api/profiles/{id}`: Delete a user profile

### Life Insurance Policy Endpoints

- `POST /api/policies/life`: Create a new life insurance policy
- `PUT /api/policies/life/{id}`: Update an existing life insurance policy
- `GET /api/policies/life/{id}`: Get a life insurance policy by ID
- `GET /api/policies/life`: Get all life insurance policies (admin only)
- `GET /api/policies/life/me`: Get life insurance policies for current user
- `DELETE /api/policies/life/{id}`: Delete a life insurance policy

Similar endpoints exist for car and house insurance policies.

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.8.x or higher
- Running Keycloak instance (version 22.x or higher)

### Configuration

The application can be configured via the `application.properties` file:

```properties
# Application configuration
spring.application.name=policy-service
server.port=8081

# Database Configuration
spring.datasource.url=jdbc:h2:mem:policydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Keycloak Configuration
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/Trustiify
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/Trustiify/protocol/openid-connect/certs
```

### Building and Running

1. Build the application:
   ```bash
   mvn clean install
   ```

2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

3. Access the API documentation:
   ```
   http://localhost:8081/swagger-ui.html
   ```

### Keycloak Integration

Before using the application, you need to set up Keycloak:

1. Create a realm (e.g., "Trustiify")
2. Create client (e.g., "policy-service")
3. Configure client settings:
   - Access Type: confidential
   - Service Accounts Enabled: true
   - Authorization Enabled: true
   - Valid Redirect URIs: http://localhost:8081/*
   - Web Origins: http://localhost:8081
4. Create roles: "user", "admin"
5. Create users and assign roles

## Development Guidelines

- Use DTOs for all API requests and responses
- Follow the existing package structure and naming conventions
- Add appropriate validation annotations to DTOs
- Use proper security annotations for authorization
- Write unit and integration tests

## API Documentation

API documentation is available via Swagger UI at:

```
http://localhost:8081/swagger-ui.html
```

## Testing the API

You can use tools like curl, Postman, or the Swagger UI to test the API.

Example token request:

```bash
curl -X POST \
  http://localhost:8080/realms/Trustiify/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&client_id=policy-service&client_secret=YOUR_CLIENT_SECRET&username=testuser&password=testuser'
```

Example API request:

```bash
curl -X GET \
  http://localhost:8081/api/profiles/me \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN'
```