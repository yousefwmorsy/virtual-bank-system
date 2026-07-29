# VBank Microservices - Architecture Diagram

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          External Clients (Web/Mobile)                          │
└─────────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────────┐
│                     WSO2 API Manager (Gateway) - Optional                       │
│                   Ports: 8243 (HTTPS), 8280 (HTTP)                              │
└─────────────────────────────────────────────────────────────────────────────────┘
                                       │
                  ┌────────────────────┼────────────────────┐
                  ▼                    ▼                    ▼
        ┌─────────────────┐  ┌─────────────────────┐  ┌──────────────┐
        │  User Service   │  │ Transaction Service │  │  BFF Service │
        │   (Port 8088)   │  │     (Port 8089)     │  │ (Port 8080)  │
        └─────────────────┘  └─────────────────────┘  └──────────────┘
              │                      │             │         │
              │                      ▼             │         │
              │              ┌──────────────────┐  │         │
              │              │  Account Service │  │         │
              │              │   (Port 8089)    │  │         │
              │              └──────────────────┘  │         │
              │                       │            │         │
              └───────────────────────┼────────────┘─────────┘
                                      │
                        ┌─────────────┴──────────────┐
                        ▼                            ▼
                ┌──────────────────┐      ┌──────────────────────┐
                │ Service Registry │      │   Kafka Broker       │
                │ (Eureka - 8761)  │      │  (Port 9092)         │
                └──────────────────┘      └──────────────────────┘
                                                   │
                                                   ▼
                                        ┌──────────────────────┐
                                        │ Logging Service      │
                                        │  (Port 8080)         │
                                        └──────────────────────┘
                                 
```

## Database Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                      PostgreSQL Databases (4 Instances)                         │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌─────────────────┐  ┌─────────────────┐  ┌────────────────┐  ┌────────────┐  │
│  │   user_db       │  │  account_db     │  │ transaction_db │  │ logging_db │  │
│  │  (Port 5051)    │  │  (Port 5050)    │  │                │  │            │  │
│  │                 │  │                 │  │                │  │            │  │
│  │ • users table   │  │ • account table │  │ • transaction  │  │ • logs     │  │
│  │   - id (UUID)   │  │   - id (UUID)   │  │   - id (UUID)  │  │   - id     │  │
│  │   - username    │  │   - userId      │  │   - from_acct  │  │   - body   │  │
│  │   - email       │  │   - account_num │  │   - to_acct    │  │   - msgType│  │
│  │   - password_h  │  │   - balance     │  │   - amount     │  │   - time   │  │
│  │   - created_at  │  │   - status      │  │   - status     │  │            │  │
│  │   - updated_at  │  │   - type        │  │   - created_at │  │            │  │
│  │                 │  │   - created_at  │  │                │  │            │  │
│  │                 │  │   - updated_at  │  │                │  │            │  │
│  └─────────────────┘  └─────────────────┘  └────────────────┘  └────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Authentication Flow

```
┌───────────────┐
│   Client      │
│ (Portal/App)  │
└───────┬───────┘
        │
        │ 1. POST /auth/login
        │    (username, password, X-Client-Type header)
        ▼
┌─────────────────────────────────────────────────────┐
│           USER-SERVICE (Port 8088)                  │
│                                                     │
│  1. Verify credentials                             │
│  2. Look up user from user_db                      │
│  3. Hash password check (Bcrypt)                   │
│  4. Generate JWT (RS256) with claims:             │
│     - userId (UUID)                                │
│     - azp (client type mapping)                   │
│     - roles (authorities)                          │
│     - exp (15 min expiration)                     │
│  5. Return AuthResponse (token, expiresIn)        │
└─────────────────────────────────────────────────────┘
        │
        │ 2. AuthResponse
        │    (JWT token, "Bearer", 900 seconds)
        ▼
┌───────────────────────────────────────────────────┐
│  Client stores JWT in local storage/memory        │
│  Includes in all subsequent requests              │
│  Authorization: Bearer <JWT>                      │
└───────────────────────────────────────────────────┘
        │
        │ 3. GET /accounts/123
        │    Authorization: Bearer <JWT>
        ▼
┌──────────────────────────────────────────────────────┐
│  ACCOUNT-SERVICE (Port 8080)                         │
│                                                      │
│  1. Extract JWT from Authorization header            │
│  2. Validate signature using User Service JWKS:      │
│     GET /.well-known/jwks.json                       │
│  3. Extract userId from JWT claims                   │
│  4. Verify account owner: accountUserId == userId    │
│  5. Return account data or 401 Unauthorized          │
└──────────────────────────────────────────────────────┘
        │
        │ 4. Response (200 OK or 401)
        ▼
┌───────────────┐
│   Client      │
└───────────────┘
```


## Service Communication Patterns

```
┌────────────────────────────────────────────────────────────────────────────┐
│                        COMMUNICATION PATTERNS                              │
├────────────────────────────────────────────────────────────────────────────┤
│                                                                            │
│ 1. SYNCHRONOUS (REST):                                                     │
│    Account-Service ──Feign──> User-Service (verify user)                   │
│    BFF-Service ─────WebClient─> Account/Transaction/User Services          │
│                                                                            │
│ 2. ASYNCHRONOUS (EVENT-DRIVEN):                                            │
│    All Services ─Kafka Producer─> Kafka Broker ─Kafka Consumer─>           │
│    [Request/Response logged]      [Topic: microservices-logs]              │
│                                   [Logging-Service consumes]               │
│                                                                            │
│ 3. SERVICE DISCOVERY:                                                      │
│    All Services ─Register/Heartbeat─> Eureka Registry                      │
│    [Service-Registry at 8761]                                              │
│                                                                            │
│ 4. SECURITY (OAuth2 JWT):                                                  │
│    Client ─Login──> User-Service ─JWT─> Client stores JWT                  │
│    Client ─Request + JWT──> Any Service                                    │
│    Service ─Validate JWT──> User-Service JWKS endpoint                     │
│                                                                            │
└────────────────────────────────────────────────────────────────────────────┘
```


## Data Flow - Dashboard Request (BFF)

```
Client Request
     │
     │ GET /bff/dashboard/user123
     │ Authorization: Bearer <JWT>
     ▼
┌─────────────────────────────────┐
│    BFF-SERVICE (Port 8080)      │
│                                 │
│ 1. Validate JWT from header     │
│ 2. Extract userId from JWT      │
│ 3. Verify userId == path param  │
└────────┬────────────────────────┘
         │
    ┌────┴───────┬───────────┬─────────────┐
    │            │           │             │
    ▼            ▼           ▼             ▼
┌────────────┐ ┌──────────┐ ┌───────────┐ ┌─────────────┐
│   User     │ │ Account  │ │Transaction│ │ Eureka Disc │
│  Service   │ │ Service  │ │ Service   │ │             │
│            │ │          │ │           │ │ resolves    │
│ GET        │ │ GET      │ │ GET       │ │ service URLs│
│ /users/... │ │/accounts │ │/trans...  │ │ from        │
│            │ │          │ │           │ │ registry    │
└────────────┘ └──────────┘ └───────────┘ └─────────────┘
    │            │           │
    │ Response   │ Response  │ Response
    │            │           │
    └────────────┴───────────┴────────────┐
                                          │
                                          ▼
                              ┌────────────────────────┐
                              │ Aggregate DashboardDTO:│
                              │ • user details         │
                              │ • all accounts         │
                              │ • transaction history  │
                              └────────────────────────┘
                                          │
                                          ▼
                              ┌────────────────────────┐
                              │ Client receives JSON   │
                              │ with consolidated data │
                              └────────────────────────┘
```