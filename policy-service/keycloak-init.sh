#!/bin/bash

# Initialize Keycloak configuration for Policy Service
# This script sets up a realm, client, roles, and test users

# Configuration variables
KEYCLOAK_URL=http://localhost:8080
REALM_NAME=Trustiify
CLIENT_ID=policy-service
CLIENT_SECRET=$(openssl rand -hex 16)
ADMIN_USER=admin
ADMIN_PASSWORD=admin

echo "Starting Keycloak initialization..."

# Get admin token
echo "Getting admin token..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=$ADMIN_USER" \
  -d "password=$ADMIN_PASSWORD" \
  -d "grant_type=password" \
  -d "client_id=admin-cli" | jq -r '.access_token')

if [ -z "$ADMIN_TOKEN" ] || [ "$ADMIN_TOKEN" == "null" ]; then
  echo "Failed to get admin token. Make sure Keycloak is running and credentials are correct."
  exit 1
fi

echo "Admin token acquired successfully."

# Create realm
echo "Creating realm: $REALM_NAME"
REALM_EXISTS=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -w "%{http_code}" -o /dev/null)

if [ "$REALM_EXISTS" == "404" ]; then
  curl -s -X POST "$KEYCLOAK_URL/admin/realms" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"realm\": \"$REALM_NAME\",
      \"enabled\": true,
      \"accessTokenLifespan\": 3600,
      \"ssoSessionIdleTimeout\": 36000,
      \"ssoSessionMaxLifespan\": 36000,
      \"displayName\": \"Trustify Insurance\"
    }"
  echo "Realm created successfully."
else
  echo "Realm already exists."
fi

# Create client
echo "Creating client: $CLIENT_ID"
curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/clients" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"clientId\": \"$CLIENT_ID\",
    \"enabled\": true,
    \"clientAuthenticatorType\": \"client-secret\",
    \"secret\": \"$CLIENT_SECRET\",
    \"redirectUris\": [\"http://localhost:8081/*\"],
    \"webOrigins\": [\"http://localhost:8081\"],
    \"publicClient\": false,
    \"protocol\": \"openid-connect\",
    \"serviceAccountsEnabled\": true,
    \"authorizationServicesEnabled\": true,
    \"directAccessGrantsEnabled\": true
  }"

echo "Client created with secret: $CLIENT_SECRET"

# Create roles
echo "Creating roles..."
ROLES=("user" "admin")

for ROLE in "${ROLES[@]}"; do
  curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/roles" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"name\": \"$ROLE\",
      \"composite\": false,
      \"clientRole\": false
    }"
  echo "Role '$ROLE' created."
done

# Create test users
echo "Creating test users..."
USERS=(
  "testuser:testuser:user"
  "adminuser:adminuser:admin,user"
)

for USER_INFO in "${USERS[@]}"; do
  IFS=: read -r USERNAME PASSWORD ROLES <<< "$USER_INFO"
  
  # Create user
  curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d "{
      \"username\": \"$USERNAME\",
      \"email\": \"$USERNAME@example.com\",
      \"emailVerified\": true,
      \"enabled\": true,
      \"firstName\": \"Test\",
      \"lastName\": \"User\",
      \"credentials\": [{
        \"type\": \"password\",
        \"value\": \"$PASSWORD\",
        \"temporary\": false
      }]
    }"
  
  echo "User '$USERNAME' created."
  
  # Get user ID
  USER_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users?username=$USERNAME" \
    -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.[0].id')
  
  # Assign roles
  IFS=',' read -r -a ROLE_ARRAY <<< "$ROLES"
  for ROLE in "${ROLE_ARRAY[@]}"; do
    # Get role ID
    ROLE_ID=$(curl -s -X GET "$KEYCLOAK_URL/admin/realms/$REALM_NAME/roles/$ROLE" \
      -H "Authorization: Bearer $ADMIN_TOKEN" | jq -r '.id')
    
    # Assign role to user
    curl -s -X POST "$KEYCLOAK_URL/admin/realms/$REALM_NAME/users/$USER_ID/role-mappings/realm" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d "[{
        \"id\": \"$ROLE_ID\",
        \"name\": \"$ROLE\",
        \"composite\": false,
        \"clientRole\": false
      }]"
    
    echo "Role '$ROLE' assigned to user '$USERNAME'."
  done
done

echo "Keycloak configuration completed successfully!"
echo ""
echo "Realm: $REALM_NAME"
echo "Client ID: $CLIENT_ID"
echo "Client Secret: $CLIENT_SECRET"
echo ""
echo "Test credentials:"
echo "- Regular user: testuser/testuser"
echo "- Admin user: adminuser/adminuser"
echo ""
echo "Token URL: $KEYCLOAK_URL/realms/$REALM_NAME/protocol/openid-connect/token"
echo ""
echo "Sample token request:"
echo "curl -X POST \\"
echo "  $KEYCLOAK_URL/realms/$REALM_NAME/protocol/openid-connect/token \\"
echo "  -H 'Content-Type: application/x-www-form-urlencoded' \\"
echo "  -d 'grant_type=password&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&username=testuser&password=testuser'"