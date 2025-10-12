#!/bin/bash

# Script to start all backend microservices in correct order
# Config Server must start first, then others

echo "=========================================="
echo "Starting Trustify Backend Services"
echo "=========================================="
echo ""

# Color codes for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if a service is running on a port
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1 ; then
        return 0
    else
        return 1
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local service_name=$1
    local port=$2
    local max_wait=$3
    local waited=0
    
    echo -e "${YELLOW}Waiting for $service_name to start on port $port...${NC}"
    
    while [ $waited -lt $max_wait ]; do
        if check_port $port; then
            echo -e "${GREEN}✓ $service_name is ready!${NC}"
            return 0
        fi
        sleep 2
        waited=$((waited + 2))
        echo -n "."
    done
    
    echo -e "${RED}✗ $service_name failed to start within ${max_wait}s${NC}"
    return 1
}

# Step 1: Start Config Server (Port 8888)
echo -e "${YELLOW}[1/5] Starting Config Server...${NC}"
cd /home/med/Documents/Trustify/config-server || exit 1

if check_port 8888; then
    echo -e "${GREEN}Config Server already running on port 8888${NC}"
else
    mvn spring-boot:run > /tmp/config-server.log 2>&1 &
    CONFIG_PID=$!
    echo "Config Server PID: $CONFIG_PID"
    
    # Wait for Config Server to be ready (critical - others depend on it)
    if ! wait_for_service "Config Server" 8888 60; then
        echo -e "${RED}Failed to start Config Server. Check logs: tail -f /tmp/config-server.log${NC}"
        exit 1
    fi
fi

sleep 5
echo ""

# Step 2: Start Discovery Server (Eureka) (Port 8761)
echo -e "${YELLOW}[2/5] Starting Discovery Server (Eureka)...${NC}"
cd /home/med/Documents/Trustify/discovery-server || exit 1

if check_port 8761; then
    echo -e "${GREEN}Discovery Server already running on port 8761${NC}"
else
    mvn spring-boot:run > /tmp/discovery-server.log 2>&1 &
    EUREKA_PID=$!
    echo "Discovery Server PID: $EUREKA_PID"
    
    if ! wait_for_service "Discovery Server" 8761 60; then
        echo -e "${RED}Failed to start Discovery Server. Check logs: tail -f /tmp/discovery-server.log${NC}"
        exit 1
    fi
fi

sleep 5
echo ""

# Step 3: Start Microservices (can run in parallel)
echo -e "${YELLOW}[3/5] Starting Microservices...${NC}"

# Policy Service (Port 8082)
echo -e "${YELLOW}  Starting Policy Service...${NC}"
cd /home/med/Documents/Trustify/policy-service || exit 1

if check_port 8082; then
    echo -e "${GREEN}  Policy Service already running on port 8082${NC}"
else
    mvn spring-boot:run > /tmp/policy-service.log 2>&1 &
    POLICY_PID=$!
    echo "  Policy Service PID: $POLICY_PID"
fi

# Claims Service (Port 8081)
echo -e "${YELLOW}  Starting Claims Service...${NC}"
cd /home/med/Documents/Trustify/claims-service || exit 1

if check_port 8081; then
    echo -e "${GREEN}  Claims Service already running on port 8081${NC}"
else
    mvn spring-boot:run > /tmp/claims-service.log 2>&1 &
    CLAIMS_PID=$!
    echo "  Claims Service PID: $CLAIMS_PID"
fi

# Notification Service (Port 8084)
echo -e "${YELLOW}  Starting Notification Service...${NC}"
cd /home/med/Documents/Trustify/notifications-service || exit 1

if check_port 8084; then
    echo -e "${GREEN}  Notification Service already running on port 8084${NC}"
else
    mvn spring-boot:run > /tmp/notifications-service.log 2>&1 &
    NOTIF_PID=$!
    echo "  Notification Service PID: $NOTIF_PID"
fi

# Wait for all microservices to be ready
echo ""
echo -e "${YELLOW}Waiting for microservices to register with Eureka...${NC}"
sleep 10

if ! wait_for_service "Policy Service" 8082 60; then
    echo -e "${RED}Policy Service failed to start. Check logs: tail -f /tmp/policy-service.log${NC}"
fi

if ! wait_for_service "Claims Service" 8081 60; then
    echo -e "${RED}Claims Service failed to start. Check logs: tail -f /tmp/claims-service.log${NC}"
fi

if ! wait_for_service "Notification Service" 8084 60; then
    echo -e "${RED}Notification Service failed to start. Check logs: tail -f /tmp/notifications-service.log${NC}"
fi

echo ""

# Step 4: Start API Gateway (Port 8083)
echo -e "${YELLOW}[4/5] Starting API Gateway...${NC}"
cd /home/med/Documents/Trustify/gateway || exit 1

if check_port 8083; then
    echo -e "${GREEN}API Gateway already running on port 8083${NC}"
else
    mvn spring-boot:run > /tmp/gateway.log 2>&1 &
    GATEWAY_PID=$!
    echo "API Gateway PID: $GATEWAY_PID"
    
    if ! wait_for_service "API Gateway" 8083 60; then
        echo -e "${RED}Failed to start API Gateway. Check logs: tail -f /tmp/gateway.log${NC}"
        exit 1
    fi
fi

sleep 5
echo ""

# Step 5: Verify Kafka is running
echo -e "${YELLOW}[5/5] Checking Kafka...${NC}"
if check_port 9092; then
    echo -e "${GREEN}✓ Kafka is running on port 9092${NC}"
else
    echo -e "${RED}✗ Kafka is NOT running on port 9092${NC}"
    echo -e "${YELLOW}Please start Kafka manually before testing notifications${NC}"
fi

echo ""
echo "=========================================="
echo -e "${GREEN}Backend Services Status:${NC}"
echo "=========================================="
echo -e "Config Server:         http://localhost:8888"
echo -e "Discovery Server:      http://localhost:8761"
echo -e "Policy Service:        http://localhost:8082"
echo -e "Claims Service:        http://localhost:8081"
echo -e "Notification Service:  http://localhost:8084"
echo -e "API Gateway:           http://localhost:8083"
echo -e "Kafka:                 localhost:9092"
echo ""
echo -e "${GREEN}All backend services are running!${NC}"
echo ""
echo "Log files location: /tmp/"
echo "  - tail -f /tmp/config-server.log"
echo "  - tail -f /tmp/discovery-server.log"
echo "  - tail -f /tmp/policy-service.log"
echo "  - tail -f /tmp/claims-service.log"
echo "  - tail -f /tmp/notifications-service.log"
echo "  - tail -f /tmp/gateway.log"
echo ""
echo "To stop all services, run: pkill -f 'spring-boot:run'"
echo "=========================================="
