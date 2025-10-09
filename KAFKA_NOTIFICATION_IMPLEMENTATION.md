# Kafka Event-Driven Notification System - Implementation Summary

## 🎯 Goal Achieved
Implemented a complete Kafka-based notification system where:
1. Claims Service publishes events when admin changes claim status
2. Notification Service consumes events and creates notifications
3. Users can retrieve their notifications via REST API

## 📋 Architecture

```
Admin changes claim status (approve/reject)
           ↓
Claims Service publishes ClaimStatusChangedEvent to Kafka
           ↓
    Kafka Broker (kafka:9092)
    Topic: claim-status-changed-events
           ↓
Notification Service consumes event
           ↓
Creates Notification in database
           ↓
User retrieves via API Gateway → /api/notifications/my
```

## 🔧 Components Created

### 1. Claims Service (Producer)
**Files:**
- `event/ClaimStatusChangedEvent.java` - JSON event model
- `kafka/ClaimEventPublisher.java` - Kafka producer
- Updated `service/impl/ClaimServiceImpl.java` - Publishes events on approve/reject

**Kafka Config (`config-repo/claims-service.properties`):**
```properties
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
kafka.topic.claim-status-changed=claim-status-changed-events
```

### 2. Notification Service (Consumer)
**Port:** 8084

**Files:**
- `model/Notification.java` - JPA entity
- `repository/NotificationRepository.java` - Data access
- `service/NotificationService.java` - Business logic
- `kafka/ClaimEventConsumer.java` - Kafka consumer with @KafkaListener
- `config/KafkaConsumerConfig.java` - JSON deserialization
- `controller/NotificationController.java` - REST endpoints
- `config/SecurityConfig.java` - OAuth2 security

**Kafka Config (`config-repo/notification-service.properties`):**
```properties
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.consumer.group-id=notification-service-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
kafka.topic.claim-status-changed=claim-status-changed-events
```

### 3. API Gateway
**Updated Routes:**
```properties
# Notification Service Route
spring.cloud.gateway.server.webmvc.routes[3].id=notification-service
spring.cloud.gateway.server.webmvc.routes[3].uri=lb://NOTIFICATION-SERVICE
spring.cloud.gateway.server.webmvc.routes[3].predicates[0]=Path=/api/notifications/**
```

## 📡 REST Endpoints

### Notification Service (via Gateway)
```
GET  /api/notifications/my          - Get all notifications for authenticated user
GET  /api/notifications/my/unread   - Get unread notifications
GET  /api/notifications/my/unread/count - Get unread count
PUT  /api/notifications/{id}/read   - Mark notification as read
PUT  /api/notifications/read-all    - Mark all as read
```

## 🔄 Event Flow

### When Admin Approves Claim:
1. Admin calls: `POST /api/admin/claims/{claimNumber}/approve`
2. Claims Service:
   - Updates claim status to APPROVED
   - Publishes `ClaimStatusChangedEvent` to Kafka:
   ```json
   {
     "claimNumber": "CLM-12345",
     "oldStatus": "UNDER_REVIEW",
     "newStatus": "APPROVED",
     "userId": "keycloak-user-id",
     "userEmail": "user@email.com",
     "timestamp": "2025-10-09T20:30:00",
     "changedBy": "admin-user-id",
     "reason": null
   }
   ```
3. Notification Service:
   - Consumes event from Kafka
   - Creates notification in database:
   ```
   Message: "Good news! Your claim CLM-12345 has been approved..."
   Type: CLAIM_APPROVED
   Status: UNREAD
   ```
4. User retrieves: `GET /api/notifications/my`

### When Admin Rejects Claim:
Similar flow but with rejection reason:
```json
{
  "newStatus": "REJECTED",
  "reason": "Insufficient documentation provided"
}
```

## 🚀 Startup Sequence

```bash
# 1. Start Config Server (port 8888)
cd config-server && nohup ./mvnw spring-boot:run > config-server.log 2>&1 &

# 2. Start Discovery Server (port 8761)
cd discovery-server && nohup ./mvnw spring-boot:run > discovery-server.log 2>&1 &

# 3. Start Services
cd policy-service && nohup ./mvnw spring-boot:run > policy-service.log 2>&1 &
cd claims-service && nohup ./mvnw spring-boot:run > claims-service.log 2>&1 &
cd notifications-service && nohup ./mvnw spring-boot:run > notifications-service.log 2>&1 &

# 4. Start Gateway (port 8083)
cd gateway && nohup ./mvnw spring-boot:run > gateway.log 2>&1 &
```

## ✅ Testing

### 1. Create Profile & Get Token
```bash
TOKEN=$(curl -s -X POST 'http://localhost:8080/realms/Trustiify/protocol/openid-connect/token' \
  -d 'client_id=Trustify-frontend' \
  -d 'grant_type=password' \
  -d 'username=testuser' \
  -d 'password=testuser' | jq -r '.access_token')
```

### 2. Admin Approves Claim (Triggers Kafka Event)
```bash
ADMIN_TOKEN=$(curl -s -X POST 'http://localhost:8080/realms/Trustiify/protocol/openid-connect/token' \
  -d 'client_id=Trustify-frontend' \
  -d 'grant_type=password' \
  -d 'username=adminuser' \
  -d 'password=adminuser' | jq -r '.access_token')

curl -X POST "http://localhost:8083/api/admin/claims/CLM-123/approve" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"claimNumber": "CLM-123", "approvedAmount": 5000, "adminNotes": "Approved"}'
```

### 3. User Checks Notifications
```bash
# Get all notifications
curl "http://localhost:8083/api/notifications/my" \
  -H "Authorization: Bearer $TOKEN"

# Get unread count
curl "http://localhost:8083/api/notifications/my/unread/count" \
  -H "Authorization: Bearer $TOKEN"
```

## 🔍 Verification

### Check Kafka Logs (Claims Service)
```bash
tail -f claims-service.log | grep -i kafka
# Should see: "Publishing claim status change event..."
# Should see: "Successfully published event for claim..."
```

### Check Kafka Logs (Notification Service)
```bash
tail -f notifications-service.log | grep -i "Received claim"
# Should see: "Received claim status change event: claimNumber=CLM-123..."
# Should see: "Successfully created notification for claim: CLM-123"
```

### Check Eureka Dashboard
```
http://localhost:8761
# Should show: NOTIFICATION-SERVICE registered
```

## 🎉 Key Features

✅ **Asynchronous Processing** - Kafka decouples services
✅ **JSON Serialization** - Easy to debug and extend
✅ **Service Discovery** - Eureka for dynamic routing
✅ **Centralized Config** - All Kafka settings in Config Server
✅ **OAuth2 Security** - JWT tokens for authentication
✅ **Type-Safe Events** - Strongly typed event models
✅ **User-Friendly Messages** - Dynamic notification text based on status
✅ **Read/Unread Tracking** - Users can mark notifications as read
✅ **Scalable** - Can add more consumers to notification-service group

## 📊 Database Schema (Notification Service)

```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    claim_number VARCHAR(255) NOT NULL,
    message VARCHAR(500) NOT NULL,
    type VARCHAR(50) NOT NULL,      -- CLAIM_APPROVED, CLAIM_REJECTED, etc.
    status VARCHAR(50) NOT NULL,    -- UNREAD, READ
    created_at TIMESTAMP NOT NULL,
    read_at TIMESTAMP
);
```

## 🔐 Security

- All endpoints require OAuth2 JWT token
- User can only see their own notifications (userId from JWT subject)
- Admin operations protected by admin role check in Claims Service

## 🎯 Next Steps (Optional Enhancements)

- [ ] Add WebSocket for real-time notification push
- [ ] Add email notifications via SMTP
- [ ] Add notification preferences (email, in-app, SMS)
- [ ] Add notification history pagination
- [ ] Add Kafka Dead Letter Queue for failed messages
- [ ] Add monitoring with Prometheus/Grafana
- [ ] Add Kafka UI for debugging (kafka-ui Docker container)

---

**Implementation Complete! 🚀**
All components are ready for testing. Start the services in order and test the end-to-end flow.
