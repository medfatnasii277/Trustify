# Policy Service Workflow Guide

This document outlines the typical workflows for using the Policy Service API.

## User Registration and Profile Management

### 1. User Authentication in Keycloak

1. Users register in Keycloak
2. Users authenticate to get a valid JWT token
3. Users include this token in all API requests

### 2. User Profile Creation

1. After authentication, create a user profile:
   ```http
   POST /api/profiles
   Authorization: Bearer <token>
   Content-Type: application/json
   
   {
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
   }
   ```

2. Get the current user's profile:
   ```http
   GET /api/profiles/me
   Authorization: Bearer <token>
   ```

3. Update the user's profile:
   ```http
   PUT /api/profiles/{id}
   Authorization: Bearer <token>
   Content-Type: application/json
   
   {
     "firstName": "John",
     "lastName": "Doe",
     "email": "john.updated@example.com",
     "phoneNumber": "0987654321",
     "dateOfBirth": "1990-01-01",
     "address": "456 New St",
     "city": "Newtown",
     "state": "NY",
     "postalCode": "54321",
     "country": "USA"
   }
   ```

## Policy Management

### 1. Life Insurance Policy

1. Create a new life insurance policy:
   ```http
   POST /api/policies/life
   Authorization: Bearer <token>
   Content-Type: application/json
   
   {
     "userProfileId": 1,
     "policyNumber": "LIFE-12345",
     "startDate": "2023-01-01",
     "endDate": "2024-01-01",
     "premium": 1000.00,
     "coverageAmount": 500000.00,
     "beneficiaryName": "Jane Doe",
     "beneficiaryRelation": "Spouse",
     "medicalHistoryDisclosed": true,
     "smoker": false
   }
   ```

2. Get all life insurance policies for the current user:
   ```http
   GET /api/policies/life/me
   Authorization: Bearer <token>
   ```

3. Get a specific life insurance policy:
   ```http
   GET /api/policies/life/{id}
   Authorization: Bearer <token>
   ```

4. Update an existing life insurance policy:
   ```http
   PUT /api/policies/life/{id}
   Authorization: Bearer <token>
   Content-Type: application/json
   
   {
     "premium": 1200.00,
     "coverageAmount": 600000.00,
     "beneficiaryName": "John Smith",
     "beneficiaryRelation": "Child"
   }
   ```

5. Delete a life insurance policy:
   ```http
   DELETE /api/policies/life/{id}
   Authorization: Bearer <token>
   ```

### 2. Car Insurance Policy

1. Create a new car insurance policy:
   ```http
   POST /api/policies/car
   Authorization: Bearer <token>
   Content-Type: application/json
   
   {
     "userProfileId": 1,
     "policyNumber": "CAR-12345",
     "startDate": "2023-01-01",
     "endDate": "2024-01-01",
     "premium": 800.00,
     "coverageAmount": 50000.00,
     "vehicleModel": "Toyota Camry",
     "vehicleYear": 2020,
     "vehicleVIN": "1HGCM82633A123456",
     "licensePlate": "ABC123"
   }
   ```

2. Similar endpoints exist for retrieving, updating, and deleting car insurance policies.

### 3. House Insurance Policy

1. Create a new house insurance policy:
   ```http
   POST /api/policies/house
   Authorization: Bearer <token>
   Content-Type: application/json
   
   {
     "userProfileId": 1,
     "policyNumber": "HOUSE-12345",
     "startDate": "2023-01-01",
     "endDate": "2024-01-01",
     "premium": 1500.00,
     "coverageAmount": 350000.00,
     "propertyAddress": "123 Home St",
     "propertyType": "Single Family",
     "buildingValue": 300000.00,
     "contentsValue": 50000.00,
     "postalCode": "12345"
   }
   ```

2. Similar endpoints exist for retrieving, updating, and deleting house insurance policies.

## Admin Workflows

### 1. User Management (Admin Only)

1. Get all user profiles:
   ```http
   GET /api/profiles
   Authorization: Bearer <admin_token>
   ```

2. Delete a user profile:
   ```http
   DELETE /api/profiles/{id}
   Authorization: Bearer <admin_token>
   ```

### 2. Policy Management (Admin Only)

1. Get all policies of a specific type:
   ```http
   GET /api/policies/life
   GET /api/policies/car
   GET /api/policies/house
   Authorization: Bearer <admin_token>
   ```

## Testing the API

Use the provided `test-api.sh` script to verify that all endpoints are working correctly:

```bash
./test-api.sh
```

This will run through all the main workflows and verify that the API is functioning as expected.