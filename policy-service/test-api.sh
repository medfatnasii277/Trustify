#!/bin/bash

# Test script for the Policy Service API
# This script tests various endpoints of the API with appropriate authentication

# Configuration variables
KEYCLOAK_URL=http://localhost:8080
POLICY_SERVICE_URL=http://localhost:8081
REALM_NAME=Trustiify
CLIENT_ID=policy-service
CLIENT_SECRET=your_client_secret_here  # Replace with the actual client secret

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}Policy Service API Test Script${NC}"
echo "==============================="
echo ""

# Function to get token
get_token() {
  local username=$1
  local password=$2
  
  echo -e "${YELLOW}Getting token for user: $username${NC}"
  
  local token_response=$(curl -s -X POST "$KEYCLOAK_URL/realms/$REALM_NAME/protocol/openid-connect/token" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    -d "grant_type=password&client_id=$CLIENT_ID&client_secret=$CLIENT_SECRET&username=$username&password=$password")
  
  local access_token=$(echo $token_response | jq -r '.access_token')
  
  if [ -z "$access_token" ] || [ "$access_token" == "null" ]; then
    echo -e "${RED}Failed to get token. Error: $(echo $token_response | jq -r '.error_description // .error')${NC}"
    return 1
  else
    echo -e "${GREEN}Token acquired successfully.${NC}"
    echo $access_token
    return 0
  fi
}

# Function to test an API endpoint
test_endpoint() {
  local method=$1
  local url=$2
  local token=$3
  local payload=$4
  local expected_status=$5
  local description=$6
  
  echo -e "\n${YELLOW}Testing: $description${NC}"
  echo "URL: $method $url"
  
  local headers=("-H" "Authorization: Bearer $token")
  if [ ! -z "$payload" ]; then
    headers+=("-H" "Content-Type: application/json")
  fi
  
  local response
  local http_code
  
  if [ "$method" == "GET" ]; then
    response=$(curl -s -w "\n%{http_code}" -X GET "$url" "${headers[@]}")
  elif [ "$method" == "POST" ]; then
    response=$(curl -s -w "\n%{http_code}" -X POST "$url" "${headers[@]}" -d "$payload")
  elif [ "$method" == "PUT" ]; then
    response=$(curl -s -w "\n%{http_code}" -X PUT "$url" "${headers[@]}" -d "$payload")
  elif [ "$method" == "DELETE" ]; then
    response=$(curl -s -w "\n%{http_code}" -X DELETE "$url" "${headers[@]}")
  fi
  
  http_code=$(echo "$response" | tail -n1)
  body=$(echo "$response" | sed '$d')
  
  if [ "$http_code" == "$expected_status" ]; then
    echo -e "${GREEN}Success! Status: $http_code${NC}"
    echo "Response: $body" | jq '.'
  else
    echo -e "${RED}Failed! Expected status $expected_status but got $http_code${NC}"
    echo "Response: $body" | jq '.'
  fi
}

# Main test sequence
echo "Step 1: Testing public endpoint"
test_endpoint "GET" "$POLICY_SERVICE_URL/api/public/health" "" "" "200" "Health Check Endpoint"

echo -e "\nStep 2: Getting tokens for test users"
USER_TOKEN=$(get_token "testuser" "testuser")
if [ $? -ne 0 ]; then exit 1; fi

ADMIN_TOKEN=$(get_token "adminuser" "adminuser")
if [ $? -ne 0 ]; then exit 1; fi

echo -e "\nStep 3: Testing authentication info endpoint"
test_endpoint "GET" "$POLICY_SERVICE_URL/api/auth-info" "$USER_TOKEN" "" "200" "Authentication Info Endpoint (User)"
test_endpoint "GET" "$POLICY_SERVICE_URL/api/auth-info" "$ADMIN_TOKEN" "" "200" "Authentication Info Endpoint (Admin)"

echo -e "\nStep 4: Testing user profile endpoints"
# Create a user profile
USER_PROFILE_PAYLOAD='{
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

test_endpoint "POST" "$POLICY_SERVICE_URL/api/profiles" "$USER_TOKEN" "$USER_PROFILE_PAYLOAD" "201" "Create User Profile"

# Get current user profile
test_endpoint "GET" "$POLICY_SERVICE_URL/api/profiles/me" "$USER_TOKEN" "" "200" "Get Current User Profile"

# List all profiles (admin only)
test_endpoint "GET" "$POLICY_SERVICE_URL/api/profiles" "$ADMIN_TOKEN" "" "200" "List All User Profiles (Admin)"
test_endpoint "GET" "$POLICY_SERVICE_URL/api/profiles" "$USER_TOKEN" "" "403" "List All User Profiles (Forbidden for Regular User)"

echo -e "\n${GREEN}All tests completed!${NC}"