# Running and Testing the Policy Service

This document provides instructions on how to run and test the Policy Service application.

## Prerequisites

Before you begin, ensure you have the following installed:
- Java 21 or higher
- Maven 3.8.x or higher
- Docker and Docker Compose (for containerized deployment)
- curl and jq (for testing)

## Running Locally with Maven

### 1. Start Keycloak

You can run Keycloak using Docker:

```bash
docker run -p 8080:8080 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:24.0.1 start-dev
```

### 2. Initialize Keycloak Configuration

Run the provided initialization script to create the realm, client, roles, and test users:

```bash
./keycloak-init.sh
```

Note the client secret that is output by the script, as you'll need it for testing.

### 3. Build and Run the Application

```bash
# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

### 4. Access the API Documentation

Open your browser and navigate to:

```
http://localhost:8081/swagger-ui.html
```

## Running with Docker Compose

### 1. Update Client Secret in Test Script

After running the `keycloak-init.sh` script, update the client secret in the `test-api.sh` script:

```bash
# Edit the script and update this line:
CLIENT_SECRET=your_client_secret_here  # Replace with the actual client secret
```

### 2. Start the Services

```bash
# Build and start the services
docker-compose up -d

# Wait for both services to fully start
```

### 3. Initialize Keycloak Configuration

```bash
# Run the initialization script
./keycloak-init.sh
```

## Testing the API

### Using the Test Script

The `test-api.sh` script will test various endpoints of the API with appropriate authentication:

```bash
./test-api.sh
```

### Manual Testing with curl

#### 1. Get a Token

```bash
curl -X POST \
  http://localhost:8080/realms/Trustiify/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password&client_id=policy-service&client_secret=YOUR_CLIENT_SECRET&username=testuser&password=testuser'
```

#### 2. Use the Token to Access Protected Endpoints

```bash
# Get current user profile
curl -X GET \
  http://localhost:8081/api/profiles/me \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN'

# Create a user profile
curl -X POST \
  http://localhost:8081/api/profiles \
  -H 'Authorization: Bearer YOUR_ACCESS_TOKEN' \
  -H 'Content-Type: application/json' \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "1234567890",
    "dateOfBirth": "1990-01-01",
    "address": "123 Main St",
    "city": "Anytown",
    "state": "CA",
    "postalCode": "12345",
    "country": "USA"
  }'
```

## Troubleshooting

### Keycloak Issues

1. **"Account is not fully set up" Error**:
   - Make sure the user account is fully set up in Keycloak
   - Ensure email verification is set to true

2. **Invalid Credentials Error**:
   - Double-check the username and password
   - Ensure the client is configured for direct grant authentication

### Policy Service Issues

1. **Connection Issues**:
   - Ensure the Keycloak URL in `application.properties` matches the running Keycloak instance
   - Check that the realm name is correct

2. **Authorization Issues**:
   - Verify that the user has the correct roles assigned
   - Check that the token contains the expected roles