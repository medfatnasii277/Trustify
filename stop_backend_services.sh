#!/bin/bash

# Script to stop all backend microservices

echo "=========================================="
echo "Stopping Trustify Backend Services"
echo "=========================================="
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Stop all Spring Boot services
echo -e "${YELLOW}Stopping all Spring Boot services...${NC}"
pkill -f 'spring-boot:run'

sleep 3

# Check if any services are still running
RUNNING=$(ps aux | grep -E '(spring-boot:run|mvn.*spring-boot)' | grep -v grep | wc -l)

if [ $RUNNING -eq 0 ]; then
    echo -e "${GREEN}✓ All backend services stopped successfully${NC}"
else
    echo -e "${RED}Warning: Some services may still be running${NC}"
    echo "Run 'ps aux | grep spring-boot' to check"
    echo "Use 'kill -9 <PID>' to force stop if needed"
fi

# Clean up log files
echo ""
echo -e "${YELLOW}Cleaning up log files...${NC}"
rm -f /tmp/config-server.log
rm -f /tmp/discovery-server.log
rm -f /tmp/policy-service.log
rm -f /tmp/claims-service.log
rm -f /tmp/notifications-service.log
rm -f /tmp/gateway.log

echo -e "${GREEN}✓ Log files cleaned${NC}"

echo ""
echo "=========================================="
echo -e "${GREEN}Backend services shutdown complete!${NC}"
echo "=========================================="
