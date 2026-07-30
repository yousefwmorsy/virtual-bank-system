# VBank Microservices - Services Documentation

## Overview
VBank is a microservices-based virtual banking system built with Spring Boot, featuring 6 microservices orchestrated through Eureka Service Registry, with event-driven communication via Apache Kafka and PostgreSQL for data persistence.

**Architecture Stack:**
- Framework: Spring Boot 4.1.0 (Java 17-26)
- Service Discovery: Netflix Eureka
- Inter-service Communication: REST (Feign), WebClient, Event Streaming (Kafka)
- Database: PostgreSQL (multiple instances)
- Security: OAuth2 JWT (RS256 algorithm)
- API Gateway: WSO2 API Manager (configured but optional)

---

## 1. USER-SERVICE

### 1.1 Overall Design
**Purpose:** Authentication, user management, and JWT token generation.

**Architecture Pattern:** Monolithic service with authentication-focused responsibilities.
- Handles user registration and login
- Generates RSA-signed JWT tokens
- Exposes JWKS endpoint for token verification by other services
- Maintains user profiles and credentials

**Dependencies:**
- Spring Boot Data JPA
- Spring Security (OAuth2)
- PostgreSQL
- Kafka (for logging)
- Eureka Client
- MapStruct (entity-to-DTO mapping)

**Port:** 8088 (configurable via `SERVER_PORT_USER`)

### 1.2 Design Choices

1. **Stateless Authentication:** Uses JWT tokens with 15-minute expiration instead of sessions.
2. **RSA Key Pair:** Asymmetric encryption for JWT signing:
   - Private key: Server-side (signs tokens)
   - Public key: Published via JWKS endpoint for verification
3. **Client Type Mapping:** Maps client applications (portal, mobile) to OAuth2 client identifiers (`azp` claim).
4. **No Role-Based Access:** Currently passes authorities as claims; roles not enforced at service level.

**Key Configuration:**
```properties
jwt.issuer=vbank-user-service
jwt.audience=vbank-client
jwt.expiration-minutes=15
jwt.key-id=user-service-key-1
wso2.client-mapping={portal:'portal-app-client', mobile:'mobile-app-client'}
```

### 1.3 Security Considerations

1. **CSRF Protection:** Disabled (stateless API doesn't require it).
2. **Session Management:** STATELESS - no server-side sessions.
3. **Password Storage:** Bcrypt hashing via Spring Security's PasswordEncoder.
4. **Public Endpoints:**
   - `/auth/login` - Requires X-Client-Type header
   - `/.well-known/jwks.json` - JWKS endpoint for token verification
   - `/users/login`, `/users/register` - Public endpoints
   - `/users/{id}/profile` - Public profile retrieval

5. **JWT Claims:**
   - `userId` - User UUID for authorization checks
   - `azp` - Authorized party (client application type)
   - `roles` - User authorities (future use for RBAC)

### 1.4 Logging and Exception Handling

**Logging Strategy:**
- APILoggingFilter: OncePerRequestFilter that captures request/response bodies
- Publishes logs to Kafka topic `microservices-logs`
- Kafka Producer Configuration:
  ```properties
  spring.kafka.producer.key-serializer=StringSerializer
  spring.kafka.producer.value-serializer=JsonSerializer
  spring.kafka.producer.properties.spring.json.add.type.headers=false
  ```

**Exception Handling:** GlobalExceptionHandler (@RestControllerAdvice)
- `UserAlreadyExistsException` → 409 Conflict
- `UserNotFoundException` → 404 Not Found
- `InvalidCredentialsException` → 401 Unauthorized
- `MethodArgumentNotValidException` → 400 Bad Request (field-level errors)
- Generic exceptions → 500 Internal Server Error

**Exception Response Format:**
```json
{
  "statusCode": 400,
  "statusReason": "Bad Request",
  "message": "field: error message",
  "timestamp": "ISO-8601 timestamp"
}
```

### 1.5 API Endpoints

| Method | Endpoint | Description | Authentication | Request | Response |
|--------|----------|-------------|-----------------|---------|----------|
| POST | `/auth/login` | Authenticate user | None (requires X-Client-Type header) | UserLogin (username, password) | AuthResponse (token, tokenType, expiresIn) |
| GET | `/.well-known/jwks.json` | Publish public keys | None | - | JWKS (keys array with public keys) |
| POST | `/users/register` | Register new user | None | UserRegister (username, email, password, firstName, lastName) | UserRegisterResponse |
| GET | `/users/{userId}/profile` | Get user profile | None | - | UserProfileResponse (id, username, email, firstName, lastName) |
| POST | `/users/login` | Legacy login (alternative) | None | UserLogin | UserLoginResponse |

**Request/Response DTOs:**
- UserLogin: username, password
- AuthResponse: token (JWT), tokenType ("Bearer"), expiresIn (seconds)
- UserRegister: username, email, password, firstName, lastName
- UserProfileResponse: id, username, email, firstName, lastName, createdAt

**Security:**
- Login endpoints: No JWT required (public)
- Profile retrieval: No JWT required (public)
- Other operations: Require JWT bearer token

### 1.6 Database Schema

**Table: users**
```sql
id UUID PRIMARY KEY
username VARCHAR UNIQUE NOT NULL
firstName VARCHAR NOT NULL
lastName VARCHAR NOT NULL
password_hash VARCHAR NOT NULL
email VARCHAR UNIQUE NOT NULL
created_at TIMESTAMP (auto-populated)
updated_at TIMESTAMP (auto-populated, immutable)
```

---

## 2. ACCOUNT-SERVICE

### 2.1 Overall Design
**Purpose:** Account management, balance operations, and inter-account transfers.

**Architecture Pattern:** Domain-driven service managing account lifecycle.
- Creates and manages bank accounts
- Tracks account balance and status
- Handles fund transfers between accounts
- Integrates with User Service for user verification
- Publishes logs to Kafka

**Dependencies:**
- Spring Boot Data JPA
- Spring Security OAuth2
- PostgreSQL
- Kafka (for logging)
- Eureka Client
- OpenAPI/Swagger (SpringDoc)
- Feign Client (for User Service communication)

**Port:** 8080 (configurable via `SERVER_PORT_ACCOUNT`)

### 2.2 Design Choices

1. **Account Number Generation:** Unique account numbers generated via AccountNumberGenerationService (max 10 retry attempts).
2. **Dual Database Strategy:** Separate PostgreSQL instance from User Service ensures data isolation.
3. **Feign Integration:** Uses declarative REST client to verify users with User Service.
4. **Transactional Transfers:** @Transactional ensures atomicity of dual-account debit/credit operations.
5. **JWT-Based Authorization:** Validates that only account owner (JWT userId) can access their accounts.

**Key Configuration:**
```yaml
account:
  number-generation-service:
    max-attempts: 10
```

### 2.3 Security Considerations

1. **Account Ownership Verification:** `@AuthenticationPrincipal Jwt jwt` extracts userId from JWT.
   ```java
   if (!fromAccount.getUserId().equals(jwt.getClaim("userId"))) {
       throw new RuntimeException("Restricted. Unauthorized account.");
   }
   ```

2. **Balance Protection:**
   - Insufficient balance check before debit
   - InsufficientBalanceException thrown if balance < transfer amount

3. **Authorization Pattern:**
   - Account creation: No JWT required (public)
   - Account retrieval: JWT required + ownership verification
   - Transfer: JWT required + ownership verification

4. **Public Endpoints:**
   - POST `/accounts` - Create account (public)
   - GET `/actuator/**` - Health checks

### 2.4 Logging and Exception Handling

**Logging Strategy:**
- Same as User Service: APILoggingFilter publishes to Kafka
- Logs include request/response bodies with timestamp

**Exception Handling:** GlobalExceptionHandler (@RestControllerAdvice)
- `AccountDoesNotExistException` → 404 Not Found
- `UserDoesNotExistException` → 404 Not Found
- `InsufficientBalanceException` → 400 Bad Request
- `MethodArgumentNotValidException` → 400 Bad Request (field-level)
- Generic exceptions → 500 Internal Server Error with logging

**Exception Response Format:**
```json
{
  "statusCode": 400,
  "statusReason": "Bad Request",
  "message": "error details"
}
```

### 2.5 API Endpoints

| Method | Endpoint | Description | Authentication | Request | Response |
|--------|----------|-------------|-----------------|---------|----------|
| POST | `/accounts` | Create account | None | CreateAccountRequestDTO | AccountSummaryDTO |
| GET | `/accounts/{account-id}` | Get account details | JWT (owner only) | - | AccountDetailsDTO |
| PUT | `/accounts/transfer` | Transfer funds | JWT (from account owner) | TransferRequestDTO | MessageDTO |

**Request/Response DTOs:**
- CreateAccountRequestDTO: userId (string), accountType (SAVINGS/CHECKING), initialBalance (BigDecimal)
- TransferRequestDTO: fromAccountId, toAccountId, amount
- AccountDetailsDTO: id, accountNumber, balance, accountType, status, userId, lastTransactionDate
- AccountSummaryDTO: id, accountNumber, balance, accountType, status
- MessageDTO: message (string)

### 2.6 Database Schema

**Table: account**
```sql
id UUID PRIMARY KEY
user_id VARCHAR NOT NULL
account_number VARCHAR UNIQUE NOT NULL
balance DECIMAL(20,2) NOT NULL
account_type VARCHAR (ENUM: SAVINGS, CHECKING)
status VARCHAR (ENUM: ACTIVE, INACTIVE, FROZEN)
last_transaction_date TIMESTAMP NOT NULL
created_at TIMESTAMP (auto-populated)
updated_at TIMESTAMP (auto-populated)
```

---

## 3. TRANSACTION-SERVICE

### 3.1 Overall Design
**Purpose:** Two-phase transaction processing for fund transfers with initiation and execution phases.

**Architecture Pattern:** Event-driven, compensating transaction pattern.
- Initiates transactions with status tracking
- Executes confirmed transactions
- Maintains transaction history
- Integrates with Account Service for balance updates
- Publishes logs to Kafka

**Dependencies:**
- Spring Boot Data JPA
- Spring Cloud (for service integration)
- PostgreSQL
- Kafka (for logging)
- Eureka Client
- MapStruct (DTO mapping)
- HTTP Client 5

**Port:** 8089 (configurable via `SERVER_PORT_TRANSACTION`)

### 3.2 Design Choices

1. **Two-Phase Transaction Model:**
   - Phase 1: Initiate - Creates transaction record with INITIATED status
   - Phase 2: Execute - Confirms transaction and updates to EXECUTED/COMPLETED
   - Allows for distributed transaction coordination

2. **Status Tracking Enum:**
   - INITIATED, PENDING, EXECUTED, COMPLETED, FAILED, ROLLED_BACK

3. **Transaction History:** Maintains immutable audit trail of all transactions.

4. **Account Service Integration:** REST-based communication for balance verification.

### 3.3 Security Considerations

1. **Token Relay:** TokenRelayInterceptor propagates JWT from client requests to downstream services.
2. **Account Verification:** Validates that accounts exist before processing.
3. **Balance Validation:** InsufficientBalanceException if source account lacks funds.
4. **Idempotency:** TransactionAlreadyCompletedException prevents duplicate execution.

### 3.4 Logging and Exception Handling

**Logging Strategy:**
- APILoggingFilter publishes request/response to Kafka
- Captures transaction lifecycle events

**Exception Handling:** GlobalExceptionHandler
- `AccountDoesNotExistException` → 404 Not Found
- `TransactionDoesNotExistException` → 404 Not Found
- `TransactionAlreadyCompletedException` → 409 Conflict
- `InsufficientBalanceException` → 400 Bad Request
- `NoTransactionsFoundException` → 404 Not Found
- `MethodArgumentNotValidException` → 400 Bad Request
- Generic exceptions → 500 Internal Server Error

### 3.5 API Endpoints

| Method | Endpoint | Description | Authentication | Request | Response |
|--------|----------|-------------|-----------------|---------|----------|
| POST | `/transactions/transfer/initiation` | Initiate transfer | None | InitiateTransactionRequest | initiateTransactionResponse |
| POST | `/transactions/transfer/execution` | Execute transfer | None | ExecutionRequest | ExecutionResponse |

**Request/Response DTOs:**
- InitiateTransactionRequest: fromAccountId, toAccountId, amount, description (optional)
- ExecutionRequest: transactionId, fromAccountId, toAccountId
- initiateTransactionResponse: transactionId, status, initiatedAt
- ExecutionResponse: transactionId, status, executedAt

### 3.6 Database Schema

**Table: transaction**
```sql
id UUID PRIMARY KEY
from_account_id UUID NOT NULL
to_account_id UUID NOT NULL
amount DECIMAL(20,2) NOT NULL
description VARCHAR (optional)
status VARCHAR (ENUM: INITIATED, PENDING, EXECUTED, COMPLETED, FAILED)
initiated_at TIMESTAMP (auto-populated)
```

---

## 4. LOGGING-SERVICE

### 4.1 Overall Design
**Purpose:** Centralized logging aggregation from all microservices via Kafka event streaming.

**Architecture Pattern:** Event consumer pattern.
- Consumes log events from Kafka topic `microservices-logs`
- Persists logs to PostgreSQL for querying and audit
- Provides centralized log storage and retrieval

**Dependencies:**
- Spring Boot Data JPA
- Spring Boot Data JDBC
- PostgreSQL
- Kafka Consumer
- Eureka Client

**Port:** 8080 (configurable via `SERVER_PORT_LOGGING`)

### 4.2 Design Choices

1. **Kafka Consumer Configuration:**
   - Topic: `microservices-logs`
   - Group ID: `microservices-logs`
   - Auto offset reset: `earliest` (replay logs on startup)
   - Key deserializer: StringDeserializer
   - Value deserializer: JsonDeserializer (LogItemDTO)

2. **Event Schema:** Each log entry contains:
   - body (request/response content)
   - messageType ("Request" or "Response")
   - timestamp (ISO-8601 format)

3. **Persistence:** Stores logs in PostgreSQL for long-term retention and querying.

### 4.3 Security Considerations

1. **No Public APIs:** Logging Service has no REST endpoints (internal-only).
2. **Kafka Access:** Consumer only; no producer access.
3. **Data Sensitivity:** Logs may contain sensitive data (passwords in plain text if not filtered upstream).

### 4.4 Logging and Exception Handling

**Kafka Listener:**
```java
@KafkaListener(topics = "microservices-logs", groupId = "microservices-logs")
public void consume(LogItemDTO logItemDTO) {
    JsonLogItem logItem = JsonLogItemMapper.toJsonLogItem(logItemDTO);
    logsRepository.save(logItem);
}
```


### 4.5 API Endpoints

**None exposed.** Logging Service is internal-only and provides no REST endpoints.

### 4.6 Database Schema

**Table: json_log_item**
```sql
id SERIAL PRIMARY KEY
body TEXT (request/response content)
message_type VARCHAR (Request or Response)
timestamp TIMESTAMP (ISO-8601 format from producer)
```

---

## 5. BFF-SERVICE (Backend for Frontend)

### 5.1 Overall Design
**Purpose:** Aggregator service for mobile/web clients to fetch consolidated user dashboard data.

**Architecture Pattern:** API Gateway pattern (orchestrator).
- Aggregates data from Account Service, Transaction Service, and User Service
- Provides single endpoint for client dashboard
- Uses reactive WebClient for async calls to downstream services
- Publishes logs to Kafka

**Dependencies:**
- Spring Boot Starter WebClient (reactive)
- Spring Cloud Eureka Client
- Spring Boot Starter Kafka
- Spring Security OAuth2

**Port:** 8080 (configurable via `SERVER_PORT_BFF`)

### 5.2 Design Choices

1. **Reactive Programming:** Uses Spring WebClient and Mono<T> for non-blocking async calls.
2. **Service Discovery:** Resolves Account Service, Transaction Service, and User Service via Eureka.
3. **Token Propagation:** Passes JWT authorization header to downstream services.
4. **Centralized Dashboard:** Combines account details, transaction history, and user profile into single response.

**ServiceResolver Pattern:**
```java
// Resolves service URLs from Eureka at runtime
eureka.instance.hostname=bff-service
eureka.client.service-url.defaultZone=http://service-registry:8761/eureka/
```

### 5.3 Security Considerations

1. **OAuth2 Resource Server:** Validates JWT from user-service JWKS endpoint.
2. **Token Relay:** Propagates JWT to downstream services via Authorization header.
3. **User ID Validation:** Endpoint parameter `userId` matched against JWT claim to prevent unauthorized data access.

**Authorization Check:**
```java
@GetMapping("/bff/dashboard/{userId}")
public Mono<ResponseEntity<DashboardDTO>> getDashboard(
    @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
    @AuthenticationPrincipal Jwt jwt, 
    @PathVariable String userId) {
    // Validates userId against jwt claim
}
```

### 5.4 Exception Handling

**Exception Handling:** GlobalExceptionHandler (@RestControllerAdvice)
- `UserNotFoundException` → 404 Not Found
- `DownstreamServiceException` → 500 Service Unavailable (if Account/Transaction service unreachable)
- Generic exceptions → 500 Internal Server Error

**Error Response Format:**
```json
{
  "statusCode": 500,
  "statusReason": "Internal Server Error",
  "message": "error description"
}
```

### 5.5 API Endpoints

| Method | Endpoint | Description | Authentication | Request | Response |
|--------|----------|-------------|-----------------|---------|----------|
| GET | `/bff/dashboard/{userId}` | Get user dashboard | JWT (verified against userId) | Authorization header (Bearer token) | DashboardDTO |

**Request/Response DTOs:**
- DashboardDTO: userDetails (UserDetailsDTO), accountList (List<AccountDetailsDTO>), transactionHistory (List<AccountTransactionsDTO>)
- UserDetailsDTO: id, username, email, firstName, lastName
- AccountDetailsDTO: id, accountNumber, balance, accountType, status, userId, lastTransactionDate
- AccountTransactionsDTO: accountId, transactions (List<TransactionHistoryResponseDTO>)
- TransactionHistoryResponseDTO: transactionId, fromAccountId, toAccountId, amount, status, timestamp

### 5.6 Database Schema

**None.** BFF Service is stateless and aggregates data from downstream services.

---

## 6. SERVICE-REGISTRY (Eureka Server)

### 6.1 Overall Design
**Purpose:** Service discovery and registration server for dynamic service location.

**Architecture Pattern:** Service Registry pattern.
- Centralized registry for all microservices
- Services register themselves on startup
- Clients discover service instances at runtime
- Heartbeat mechanism to detect failed instances

**Framework:** Netflix Eureka Server

**Port:** 8761 (configurable via `SERVER_PORT_REGISTRY`)

### 6.2 Design Choices

1. **Self-Registration:** Services auto-register via @EnableEurekaClient.
2. **Heartbeat Monitoring:** Detects service health via periodic pings.
3. **Eureka Client Configuration:**
   ```properties
   eureka.instance.hostname={service-name}
   eureka.instance.prefer-ip-address=true
   eureka.client.service-url.defaultZone=http://service-registry:8761/eureka/
   ```

---

## 7. CROSS-CUTTING CONCERNS

### 7.1 Event-Driven Architecture (Kafka)

**Topic Configuration:**
- Topic Name: `microservices-logs`
- Partitions: 1 (single-partition for ordered delivery)
- Replication Factor: 1
- Auto-create: Enabled

**Producer Configuration (All Services except Logging):**
```properties
spring.kafka.bootstrap-servers=kafka:9092
spring.kafka.producer.key-serializer=StringSerializer
spring.kafka.producer.value-serializer=JsonSerializer
spring.kafka.producer.properties.spring.json.add.type.headers=false
```

**Consumer Configuration (Logging Service):**
```yaml
spring.kafka.consumer:
  group-id: microservices-logs
  auto-offset-reset: earliest
  key-deserializer: StringDeserializer
  value-deserializer: JsonDeserializer
  properties:
    spring.json.value.default.type: LogItemDTO
    spring.json.use.type.headers: false
```

### 7.2 Security Architecture

**OAuth2 JWT Flow:**
1. User logs in via User Service with X-Client-Type header
2. User Service generates RS256-signed JWT with:
   - `userId` claim (for authorization)
   - `azp` claim (authorized party/client type)
   - `roles` claim (for future RBAC)
3. Client includes JWT in Authorization: Bearer header
4. Resource services validate JWT via User Service's JWKS endpoint
5. Extract userId from JWT for authorization checks

**Key Management:**
- Private keys: Stored server-side (User Service)
- Public keys: Published via `/.well-known/jwks.json` endpoint
- Algorithm: RS256 (RSA Signature with SHA-256)

### 7.3 Database Architecture

**Per-Service Database Pattern:**
- Each microservice has dedicated PostgreSQL instance
- Database names: `user_db`, `account_db`, `transaction_db`, `logging_db`
- Hibernat DDL: `ddl-auto: update` (auto-schema evolution)
- No cross-database joins; services communicate via REST/Kafka

**Connection Pool:**
```yaml
spring.datasource:
  url: jdbc:postgresql://{host}:{port}/{db_name}
  username: {user}
  password: {password}
  driver-class-name: org.postgresql.Driver
```

### 7.4 API Gateway Integration

**WSO2 API Manager** (configured but optional)
- Acts as reverse proxy for external client access
- Provides API versioning and rate limiting
- Ports: 8243 (HTTPS), 8280 (HTTP), 9443, 9763 (management)
- Configuration: Defined in docker-compose.yml

### 7.5 Deployment Architecture

**Docker Compose Profiles:**
```bash
docker compose up                           # Infrastructure only (DBs + Eureka)
docker compose --profile services up        # All services + infrastructure
docker compose --profile logging up         # Kafka + infrastructure
docker compose --profile all up             # Everything
```

**Service Dependencies:**
```
Account Service → User Service (Feign), Account DB, Eureka, Kafka
User Service → User DB, Eureka, Kafka
Transaction Service → Account Service, Transaction DB, Eureka, Kafka
Logging Service → Logging DB, Kafka
BFF Service → Account Service, Transaction Service, User Service, Eureka, Kafka
```

---

## 8. SUMMARY TABLE

| Service | Port | Database | Authentication | Logging | Key Dependencies |
|---------|------|----------|-----------------|---------|------------------|
| User Service | 8088 | PostgreSQL (user_db) | JWT Generator | Kafka | Spring Security, Eureka |
| Account Service | 8080 | PostgreSQL (account_db) | JWT Validator | Kafka | Feign, Eureka, JPA |
| Transaction Service | 8089 | PostgreSQL (transaction_db) | Token Relay | Kafka | WebClient, Eureka, JPA |
| Logging Service | 8080 | PostgreSQL (logging_db) | None | Kafka Consumer | Kafka, JPA |
| BFF Service | 8080 | None (aggregator) | JWT Validator | Kafka | WebClient, Eureka |
| Service Registry | 8761 | None (in-memory) | None | Standard | Eureka Server |

---

