#!/bin/bash

echo "=================================================="
echo "   Kafka Notification System - End-to-End Test"
echo "=================================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
RED='\033[0;31m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

GATEWAY="http://localhost:8083"
KEYCLOAK="http://localhost:8080"

echo -e "${BLUE}Step 1: Getting Authentication Tokens${NC}"
echo "----------------------------------------------"

# Get User Token (testuser/testuser)
echo "Getting token for testuser..."
USER_TOKEN=$(curl -s -X POST "$KEYCLOAK/realms/Trustiify/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=Trustify-frontend" \
  -d "grant_type=password" \
  -d "username=testuser" \
  -d "password=testuser" \
  -d "scope=openid" | jq -r '.access_token')

if [ "$USER_TOKEN" == "null" ] || [ -z "$USER_TOKEN" ]; then
    echo -e "${RED}❌ Failed to get user token${NC}"
    exit 1
fi
echo -e "${GREEN}✅ User token obtained: ${USER_TOKEN:0:30}...${NC}"

# Get Admin Token (admintest/admintest)
echo "Getting token for admintest..."
ADMIN_TOKEN=$(curl -s -X POST "$KEYCLOAK/realms/Trustiify/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=Trustify-frontend" \
  -d "grant_type=password" \
  -d "username=admintest" \
  -d "password=admintest" \
  -d "scope=openid" | jq -r '.access_token')

if [ "$ADMIN_TOKEN" == "null" ] || [ -z "$ADMIN_TOKEN" ]; then
    echo -e "${RED}❌ Failed to get admin token${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Admin token obtained: ${ADMIN_TOKEN:0:30}...${NC}"
echo ""

echo -e "${BLUE}Step 2: Checking Existing User Profile${NC}"
echo "----------------------------------------------"
PROFILE_RESPONSE=$(curl -s -X GET "$GATEWAY/api/profiles/my" \
  -H "Authorization: Bearer $USER_TOKEN")

echo "$PROFILE_RESPONSE" | jq .
PROFILE_ID=$(echo "$PROFILE_RESPONSE" | jq -r '.id // empty')

if [ -z "$PROFILE_ID" ]; then
    echo -e "${YELLOW}⚠️  Profile doesn't exist, creating new one...${NC}"
    PROFILE_RESPONSE=$(curl -s -X POST "$GATEWAY/api/profiles" \
      -H "Authorization: Bearer $USER_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "firstName": "John",
        "lastName": "Doe",
        "dateOfBirth": "1990-01-15",
        "phoneNumber": "+1234567890",
        "address": "123 Main Street",
        "city": "New York",
        "state": "NY",
        "zipCode": "10001",
        "country": "USA",
        "employmentStatus": "EMPLOYED",
        "occupation": "Software Engineer",
        "company": "Tech Corp",
        "annualIncome": 85000
      }')
    PROFILE_ID=$(echo "$PROFILE_RESPONSE" | jq -r '.id // empty')
fi
echo -e "${GREEN}✅ User Profile ID: $PROFILE_ID${NC}"
echo ""

echo -e "${BLUE}Step 3: Checking Admin Profile${NC}"
echo "----------------------------------------------"
ADMIN_PROFILE_RESPONSE=$(curl -s -X GET "$GATEWAY/api/profiles/my" \
  -H "Authorization: Bearer $ADMIN_TOKEN")

echo "$ADMIN_PROFILE_RESPONSE" | jq .
ADMIN_PROFILE_ID=$(echo "$ADMIN_PROFILE_RESPONSE" | jq -r '.id // empty')

if [ -z "$ADMIN_PROFILE_ID" ]; then
    echo -e "${YELLOW}⚠️  Admin profile doesn't exist, creating new one...${NC}"
    ADMIN_PROFILE_RESPONSE=$(curl -s -X POST "$GATEWAY/api/profiles" \
      -H "Authorization: Bearer $ADMIN_TOKEN" \
      -H "Content-Type: application/json" \
      -d '{
        "firstName": "Admin",
        "lastName": "User",
        "dateOfBirth": "1985-05-20",
        "phoneNumber": "+1987654321",
        "address": "456 Admin Ave",
        "city": "Boston",
        "state": "MA",
        "zipCode": "02101",
        "country": "USA",
        "employmentStatus": "EMPLOYED",
        "occupation": "Insurance Manager",
        "company": "Trustify Inc",
        "annualIncome": 120000
      }')
    ADMIN_PROFILE_ID=$(echo "$ADMIN_PROFILE_RESPONSE" | jq -r '.id // empty')
fi
echo -e "${GREEN}✅ Admin Profile ID: $ADMIN_PROFILE_ID${NC}"
echo ""

echo -e "${BLUE}Step 4: Creating a Car Policy (as User)${NC}"
echo "----------------------------------------------"
POLICY_RESPONSE=$(curl -s -X POST "$GATEWAY/api/policies/car" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "startDate": "2025-10-11",
    "endDate": "2026-10-11",
    "paymentFrequency": "MONTHLY",
    "coverageAmount": 50000,
    "description": "Comprehensive car insurance policy",
    "vehicleMake": "Toyota",
    "vehicleModel": "Camry",
    "vehicleYear": 2022,
    "vehicleVIN": "1HGBH41JXMN109186",
    "licensePlate": "ABC-1234",
    "coverageType": "COMPREHENSIVE",
    "includesRoadSideAssistance": true,
    "includesRentalCarCoverage": true
  }')

echo "$POLICY_RESPONSE" | jq .
POLICY_NUMBER=$(echo "$POLICY_RESPONSE" | jq -r '.policyNumber // empty')

if [ -z "$POLICY_NUMBER" ]; then
    echo -e "${RED}❌ Failed to create policy${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Policy created: $POLICY_NUMBER${NC}"
echo ""

echo -e "${BLUE}Step 5: Creating a Claim (as User)${NC}"
echo "----------------------------------------------"
CLAIM_RESPONSE=$(curl -s -X POST "$GATEWAY/api/claims" \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"policyNumber\": \"$POLICY_NUMBER\",
    \"policyType\": \"CAR\",
    \"claimType\": \"ACCIDENT_CLAIM\",
    \"incidentDate\": \"2025-10-01\",
    \"claimedAmount\": 5000,
    \"description\": \"Front bumper and headlight damaged in parking lot accident. Vehicle was parked when another car backed into it.\",
    \"incidentLocation\": \"Shopping Mall Parking Lot, New York, NY\",
    \"severity\": \"MEDIUM\"
  }")

echo "$CLAIM_RESPONSE" | jq .
CLAIM_NUMBER=$(echo "$CLAIM_RESPONSE" | jq -r '.claimNumber // empty')

if [ -z "$CLAIM_NUMBER" ]; then
    echo -e "${RED}❌ Failed to create claim${NC}"
    exit 1
fi
echo -e "${GREEN}✅ Claim created: $CLAIM_NUMBER${NC}"
echo ""

echo -e "${YELLOW}⏳ Waiting 2 seconds before reviewing claim...${NC}"
sleep 2
echo ""

echo -e "${BLUE}Step 6: Moving Claim to Under Review (as Admin)${NC}"
echo "----------------------------------------------"
REVIEW_RESPONSE=$(curl -s -X PATCH "$GATEWAY/api/admin/claims/$CLAIM_NUMBER/under-review" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json")

echo "$REVIEW_RESPONSE" | jq .
echo -e "${GREEN}✅ Claim moved to UNDER_REVIEW status${NC}"
echo ""

echo -e "${YELLOW}⏳ Waiting 2 seconds before approving claim...${NC}"
sleep 2
echo ""

echo -e "${BLUE}Step 7: Approving Claim (as Admin) - This triggers Kafka event${NC}"
echo "----------------------------------------------"
APPROVE_RESPONSE=$(curl -s -X PATCH "$GATEWAY/api/admin/claims/$CLAIM_NUMBER/approve" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"claimNumber\": \"$CLAIM_NUMBER\",
    \"approvedAmount\": 4500,
    \"adminNotes\": \"Approved with minor deduction for wear and tear\"
  }")

echo "$APPROVE_RESPONSE" | jq .
echo -e "${GREEN}✅ Claim approved! Kafka event should be published now.${NC}"
echo ""

echo -e "${YELLOW}⏳ Waiting 5 seconds for Kafka event to be processed...${NC}"
sleep 5
echo ""

echo -e "${BLUE}Step 8: Checking User Notifications${NC}"
echo "----------------------------------------------"
NOTIFICATIONS=$(curl -s -X GET "$GATEWAY/api/notifications/my" \
  -H "Authorization: Bearer $USER_TOKEN")

echo "$NOTIFICATIONS" | jq .

NOTIFICATION_COUNT=$(echo "$NOTIFICATIONS" | jq 'length')
echo ""
echo -e "${BLUE}Notification Count: $NOTIFICATION_COUNT${NC}"

if [ "$NOTIFICATION_COUNT" -gt 0 ]; then
    echo -e "${GREEN}✅ SUCCESS! Notifications received!${NC}"
    echo ""
    echo -e "${BLUE}Latest Notification Details:${NC}"
    echo "$NOTIFICATIONS" | jq '.[0]'
else
    echo -e "${RED}❌ FAILED! No notifications found.${NC}"
    exit 1
fi
echo ""

echo -e "${BLUE}Step 9: Getting Unread Notification Count${NC}"
echo "----------------------------------------------"
UNREAD_COUNT=$(curl -s -X GET "$GATEWAY/api/notifications/my/unread/count" \
  -H "Authorization: Bearer $USER_TOKEN")

echo "$UNREAD_COUNT" | jq .
echo ""

echo "=================================================="
echo -e "${GREEN}   ✅ END-TO-END TEST COMPLETED SUCCESSFULLY!${NC}"
echo "=================================================="
echo ""
echo "Summary:"
echo "  - User Profile: $PROFILE_ID"
echo "  - Policy Number: $POLICY_NUMBER"
echo "  - Claim Number: $CLAIM_NUMBER"
echo "  - Notifications: $NOTIFICATION_COUNT"
echo ""
echo -e "${GREEN}Kafka event-driven notification system is working!${NC}"
