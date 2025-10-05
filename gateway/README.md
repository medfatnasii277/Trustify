# Trustify API Gateway

## Overview
The API Gateway is the single entry point for all client requests to the Trustify microservices architecture. It provides centralized authentication, authorization, routing, and security.

## Architecture

```
Frontend (Angular)              API Gateway (Port 8083)              Backend Services
   Port 4200                         ↓                                    ↓
       ↓                      ┌──────────────────┐                        ↓
       └──────────────────────→│  Security Layer  │                        ↓
                              │  (JWT Validation)│                        ↓
                              └──────────────────┘                        ↓
                                       ↓                                   ↓
                              ┌──────────────────┐                        ↓
                              │  Role Extraction │                        ↓
                              │  (user/admin)    │                        ↓
                              └──────────────────┘                        ↓
                                       ↓                                   ↓
                              ┌──────────────────┐                        ↓
                              │    Routing       │                        ↓
                              └──────────────────┘                        ↓
                                       ↓                                   ↓
                    ┌──────────────────┼──────────────────┐              ↓
                    ↓                  ↓                   ↓              ↓
            Policy Service      Claims Service      Claims Admin         ↓
            Port 8081           Port 8082           Port 8082            ↓
            /api/policies/**    /api/claims/**      /api/admin/claims/** ↓
```

## Features

### 1. **Centralized Authentication**
- JWT token validation using Keycloak
- First line of defense before requests reach backend services
- Validates token signature, expiration, and issuer

### 2. **Role-Based Access Control**
- Extracts roles from Keycloak JWT (`realm_access.roles`)
- Converts to Spring Security authorities (e.g., `ROLE_USER`, `ROLE_ADMIN`)
- Enforces role-based routing at gateway level

### 3. **Intelligent Routing**
- `/api/policies/**` → Policy Service (port 8081)
- `/api/claims/**` → Claims Service (port 8082) - User endpoints
- `/api/admin/claims/**` → Claims Service (port 8082) - Admin endpoints (requires ROLE_ADMIN)

### 4. **Request Enhancement**
- Adds custom headers to all forwarded requests:
  - `X-User-Id`: Keycloak user ID (sub claim)
  - `X-User-Email`: User email
  - `X-User-Roles`: Comma-separated list of roles
  - `X-User-Preferred-Username`: Username

### 5. **CORS Management**
- Centralized CORS configuration
- Allows requests from frontend (http://localhost:4200)
- Handles preflight OPTIONS requests

## Configuration

### Port
- Gateway runs on port **8083**

### Keycloak Integration
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/Trustiify
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://localhost:8080/realms/Trustiify/protocol/openid-connect/certs
```

### Routes Configuration
Routes are defined in `application.properties`:
```properties
# Policy Service
spring.cloud.gateway.mvc.routes[0].uri=http://localhost:8081
spring.cloud.gateway.mvc.routes[0].predicates[0]=Path=/api/policies/**

# Claims Service - User
spring.cloud.gateway.mvc.routes[1].uri=http://localhost:8082
spring.cloud.gateway.mvc.routes[1].predicates[0]=Path=/api/claims/**

# Claims Service - Admin
spring.cloud.gateway.mvc.routes[2].uri=http://localhost:8082
spring.cloud.gateway.mvc.routes[2].predicates[0]=Path=/api/admin/claims/**
```

## Security Flow

### 1. Request Arrives at Gateway
```
Frontend → Gateway (with JWT Bearer token in Authorization header)
```

### 2. Token Validation
```java
OAuth2ResourceServer validates JWT:
- Check signature using Keycloak JWK
- Verify issuer (http://localhost:8080/realms/Trustiify)
- Check expiration time
- Extract claims
```

### 3. Role Extraction
```java
KeycloakRealmRoleConverter extracts roles:
JWT: { "realm_access": { "roles": ["user", "admin"] } }
       ↓
Spring Security: [ROLE_USER, ROLE_ADMIN]
```

### 4. Authorization Check
```java
SecurityFilterChain checks endpoint access:
- /api/admin/** → requires ROLE_ADMIN
- /api/policies/** → requires authentication
- /api/claims/** → requires authentication
```

### 5. Request Enhancement
```java
GatewayFilterConfig adds headers:
X-User-Id: a1b2c3d4-5678-90ef-ghij-klmnopqrstuv
X-User-Email: user@example.com
X-User-Roles: ROLE_USER,ROLE_ADMIN
X-User-Preferred-Username: testuser
```

### 6. Route to Backend
```
Gateway → Backend Service (with enhanced request)
```

### 7. Backend Security (Second Layer)
```java
Backend services still validate JWT and use @PreAuthorize
This provides defense in depth
```

## Roles

### USER Role
- Can access all policy endpoints
- Can submit and view own claims
- Can cancel own claims

### ADMIN Role
- Has all USER permissions
- Can view all claims
- Can approve, reject, and settle claims
- Can access admin statistics

## API Endpoints

### Gateway Information
- `GET /gateway/info` - Gateway service information
- `GET /gateway/me` - Current authenticated user information

### Health & Monitoring
- `GET /actuator/health` - Health check
- `GET /actuator/gateway/routes` - View all configured routes

### Routed Endpoints
- `GET/POST/PUT/DELETE /api/policies/**` - Policy operations
- `GET/POST/PATCH /api/claims/**` - Claims operations (user)
- `GET/PATCH /api/admin/claims/**` - Claims operations (admin only)

## Running the Gateway

### Prerequisites
- Java 17
- Keycloak running on port 8080
- Policy Service running on port 8081
- Claims Service running on port 8082

### Start Gateway
```bash
cd gateway
./mvnw spring-boot:run
```

Gateway will start on port 8083.

### Verify Routes
```bash
curl http://localhost:8083/actuator/gateway/routes
```

### Test Authentication
```bash
# Get token from Keycloak
TOKEN=$(curl -X POST http://localhost:8080/realms/Trustiify/protocol/openid-connect/token \
  -d "client_id=policy-service" \
  -d "username=testuser" \
  -d "password=testuser" \
  -d "grant_type=password" | jq -r '.access_token')

# Test user endpoint through gateway
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8083/api/claims/my-claims

# Test admin endpoint through gateway (will fail if not admin)
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8083/api/admin/claims
```

## Frontend Integration

Update `environment.ts`:
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8083/api', // Gateway URL
  keycloak: {
    url: 'http://localhost:8080',
    realm: 'Trustiify',
    clientId: 'Trustify-frontend'
  }
};
```

All frontend requests now go through the gateway, which handles:
- JWT validation
- Role-based access control
- Routing to appropriate service
- Adding user context headers

## Benefits

1. **Single Entry Point**: Clients only need to know one URL
2. **Centralized Security**: Authentication/authorization in one place
3. **Defense in Depth**: Gateway + service-level security
4. **Easy Service Discovery**: Add new services by updating gateway routes
5. **Request/Response Transformation**: Add headers, modify requests
6. **Monitoring**: Centralized logging and metrics
7. **Rate Limiting**: Can add rate limiting at gateway level (future)
8. **API Versioning**: Can route different API versions (future)

## Troubleshooting

### 401 Unauthorized
- Check JWT token is valid
- Verify Keycloak is running on port 8080
- Check token hasn't expired

### 403 Forbidden
- Verify user has required role (USER or ADMIN)
- Check role extraction in gateway logs
- Ensure role names match (case-sensitive)

### 404 Not Found
- Verify route configuration in application.properties
- Check backend service is running
- Review gateway logs for routing decisions

### CORS Errors
- Verify CORS configuration in SecurityConfig
- Check frontend origin matches allowed origins
- Ensure preflight requests are handled

## Logs
Enable debug logging:
```properties
logging.level.org.springframework.cloud.gateway=DEBUG
logging.level.org.springframework.security=DEBUG
```

View token claims and routing decisions in logs.
