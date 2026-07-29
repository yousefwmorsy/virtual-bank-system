# VBank Microservices - API Reference Guide

## Quick Start

### Base URLs
```
User Service:        http://localhost:8088
Account Service:     http://localhost:8080
Transaction Service: http://localhost:8089
Logging Service:     http://localhost:8084
BFF Service:         http://localhost:8085
Service Registry:    http://localhost:8761
Kafka UI:            http://localhost:8073
WSO2 Gateway:        https://localhost:8243 (HTTPS)
```

### Common Headers
```
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>  (for protected endpoints)
X-Client-Type: portal|mobile       (required for login)
```

---

## USER-SERVICE (Port 8088)

### 1. Login

**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and receive JWT token

**Headers:**
```
Content-Type: application/json
X-Client-Type: portal (or mobile)
```

**Request:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Error Responses:**
- 400 Bad Request: Missing/invalid username or password
- 401 Unauthorized: Invalid credentials
- 400 Bad Request: Missing X-Client-Type header or invalid client type

---

### 2. Register User

**Endpoint:** `POST /users/register`

**Description:** Create new user account

**Request:**
```json
{
  "username": "jane_smith",
  "email": "jane@example.com",
  "password": "SecurePass123!",
  "firstName": "Jane",
  "lastName": "Smith"
}
```

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "jane_smith",
  "email": "jane@example.com",
  "firstName": "Jane",
  "lastName": "Smith"
}
```

**Error Responses:**
- 409 Conflict: User already exists
- 400 Bad Request: Invalid input format

---

### 3. Get User Profile

**Endpoint:** `GET /users/{userId}/profile`

**Description:** Retrieve user profile information

**Path Parameters:**
- `userId` (string, UUID): User ID

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "username": "jane_smith",
  "email": "jane@example.com",
  "firstName": "Jane",
  "lastName": "Smith",
  "createdAt": "2024-07-29T10:15:30Z"
}
```

**Error Responses:**
- 404 Not Found: User not found

---

### 4. Get JWKS (Public Keys)

**Endpoint:** `GET /.well-known/jwks.json`

**Description:** Retrieve public keys for JWT verification (used by other services)

**Success Response (200 OK):**
```json
{
  "keys": [
    {
      "kty": "RSA",
      "use": "sig",
      "kid": "user-service-key-1",
      "n": "xGOr...",
      "e": "AQAB",
      "alg": "RS256"
    }
  ]
}
```

---

## ACCOUNT-SERVICE (Port 8080)

### 1. Create Account

**Endpoint:** `POST /accounts`

**Description:** Create new bank account

**Request:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "accountType": "SAVINGS",
  "initialBalance": 1000.00
}
```

**Success Response (200 OK):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "accountNumber": "ACC1234567890",
  "balance": 1000.00,
  "accountType": "SAVINGS",
  "status": "ACTIVE"
}
```

**Error Responses:**
- 404 Not Found: User doesn't exist
- 400 Bad Request: Invalid account type

**Account Types:** SAVINGS, CHECKING

---

### 2. Get Account Details

**Endpoint:** `GET /accounts/{accountId}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
```

**Description:** Retrieve account information (account owner only)

**Path Parameters:**
- `accountId` (string): Account ID

**Success Response (200 OK):**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "accountNumber": "ACC1234567890",
  "balance": 1000.00,
  "accountType": "SAVINGS",
  "status": "ACTIVE",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "lastTransactionDate": "2024-07-29T15:30:00Z",
  "createdAt": "2024-07-29T10:00:00Z",
  "updatedAt": "2024-07-29T15:30:00Z"
}
```

**Error Responses:**
- 401 Unauthorized: Invalid or missing JWT
- 403 Forbidden: Not account owner
- 404 Not Found: Account doesn't exist

---

### 3. Transfer Funds

**Endpoint:** `PUT /accounts/transfer`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Description:** Transfer money between accounts (from account owner only)

**Request:**
```json
{
  "fromAccountId": "660e8400-e29b-41d4-a716-446655440001",
  "toAccountId": "770e8400-e29b-41d4-a716-446655440002",
  "amount": 100.50
}
```

**Success Response (200 OK):**
```json
{
  "message": "Account updated successfully"
}
```

**Error Responses:**
- 401 Unauthorized: Invalid or missing JWT
- 403 Forbidden: Not source account owner
- 404 Not Found: Source or destination account doesn't exist
- 400 Bad Request: Insufficient balance
- 400 Bad Request: Invalid transfer amount

**Business Rules:**
- Source account owner (JWT userId) must match account owner
- Sufficient balance required in source account
- Amount must be > 0
- Both accounts must exist and be ACTIVE

---

## TRANSACTION-SERVICE (Port 8089)

### 1. Initiate Transaction

**Endpoint:** `POST /transactions/transfer/initiation`

**Description:** Start a two-phase transfer process

**Request:**
```json
{
  "fromAccountId": "660e8400-e29b-41d4-a716-446655440001",
  "toAccountId": "770e8400-e29b-41d4-a716-446655440002",
  "amount": 50.00,
  "description": "Monthly rent payment"
}
```

**Success Response (200 OK):**
```json
{
  "transactionId": "880e8400-e29b-41d4-a716-446655440003",
  "status": "INITIATED",
  "initiatedAt": "2024-07-29T16:45:00Z"
}
```

**Error Responses:**
- 400 Bad Request: Invalid input
- 404 Not Found: Account not found

**Status Enum:** INITIATED, PENDING, EXECUTED, COMPLETED, FAILED

---

### 2. Execute Transaction

**Endpoint:** `POST /transactions/transfer/execution`

**Description:** Complete a previously initiated transaction

**Request:**
```json
{
  "transactionId": "880e8400-e29b-41d4-a716-446655440003",
  "fromAccountId": "660e8400-e29b-41d4-a716-446655440001",
  "toAccountId": "770e8400-e29b-41d4-a716-446655440002"
}
```

**Success Response (200 OK):**
```json
{
  "transactionId": "880e8400-e29b-41d4-a716-446655440003",
  "status": "COMPLETED",
  "executedAt": "2024-07-29T16:45:30Z"
}
```

**Error Responses:**
- 404 Not Found: Transaction not found
- 409 Conflict: Transaction already completed
- 400 Bad Request: Insufficient balance

---

## BFF-SERVICE (Port 8080)

### 1. Get Dashboard

**Endpoint:** `GET /bff/dashboard/{userId}`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Description:** Get consolidated dashboard for user (aggregates data from Account, Transaction, and User services)

**Path Parameters:**
- `userId` (string, UUID): User ID

**Success Response (200 OK):**
```json
{
  "userDetails": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "username": "jane_smith",
    "email": "jane@example.com",
    "firstName": "Jane",
    "lastName": "Smith"
  },
  "accountList": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "accountNumber": "ACC1234567890",
      "balance": 950.50,
      "accountType": "SAVINGS",
      "status": "ACTIVE",
      "userId": "550e8400-e29b-41d4-a716-446655440000",
      "lastTransactionDate": "2024-07-29T16:45:30Z"
    }
  ],
  "transactionHistory": [
    {
      "accountId": "660e8400-e29b-41d4-a716-446655440001",
      "transactions": [
        {
          "transactionId": "880e8400-e29b-41d4-a716-446655440003",
          "fromAccountId": "660e8400-e29b-41d4-a716-446655440001",
          "toAccountId": "770e8400-e29b-41d4-a716-446655440002",
          "amount": 50.00,
          "status": "COMPLETED",
          "timestamp": "2024-07-29T16:45:30Z"
        }
      ]
    }
  ]
}
```

**Error Responses:**
- 401 Unauthorized: Invalid or missing JWT
- 403 Forbidden: JWT userId doesn't match path parameter
- 404 Not Found: User not found
- 503 Service Unavailable: Downstream service unreachable

---

## LOGGING-SERVICE (Port 8084)

**Note:** Logging Service is internal-only and provides no public REST API.

**Function:** Consumes log events from Kafka and persists them to PostgreSQL.

**Kafka Topic:** `microservices-logs`

**Consumer Group:** `microservices-logs`

**Message Format:**
```json
{
  "body": "request/response body content",
  "messageType": "Request|Response",
  "timestamp": "2024-07-29T16:45:30.123Z"
}
```

---

## Common Error Response Format

All services return errors in this format:

```json
{
  "statusCode": 400,
  "statusReason": "Bad Request",
  "message": "Detailed error message",
  "timestamp": "2024-07-29T16:45:30Z"
}
```

---

## Authentication Flow Example

### Step 1: Login
```bash
curl -X POST http://localhost:8088/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Client-Type: portal" \
  -d '{
    "username": "jane_smith",
    "password": "SecurePass123!"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Step 2: Use Token for Protected Request
```bash
curl -X GET http://localhost:8080/accounts/660e8400-e29b-41d4-a716-446655440001 \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Response:**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "accountNumber": "ACC1234567890",
  "balance": 1000.00,
  ...
}
```

## Environment Variables

### Database Configuration
```bash
DB_USER_HOST=user-db
DB_USER_PORT=5432
DB_USER_NAME=user_db
DB_USER_USER=user_user
DB_USER_PASS=user_pass

DB_ACCOUNT_HOST=account-db
DB_ACCOUNT_PORT=5432
DB_ACCOUNT_NAME=account_db
DB_ACCOUNT_USER=account_user
DB_ACCOUNT_PASS=account_pass

DB_TRANSACTION_HOST=transaction-db
DB_TRANSACTION_PORT=5432
DB_TRANSACTION_NAME=transaction_db
DB_TRANSACTION_USER=transaction_user
DB_TRANSACTION_PASS=transaction_pass

DB_LOGGING_HOST=logging-db
DB_LOGGING_PORT=5432
DB_LOGGING_NAME=logging_db
DB_LOGGING_USER=logging_user
DB_LOGGING_PASS=logging_pass
```

### Service Ports
```bash
SERVER_PORT_REGISTRY=8761
SERVER_PORT_ACCOUNT=8080
SERVER_PORT_USER=8088
SERVER_PORT_TRANSACTION=8089
SERVER_PORT_LOGGING=8080
SERVER_PORT_BFF=8080
```

### Eureka Configuration
```bash
EUREKA_HOST=service-registry
EUREKA_PORT=8761
```

### Kafka Configuration
```bash
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

