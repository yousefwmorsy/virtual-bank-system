# VBank - Virtual Banking Microservices System

A modern, scalable microservices-based virtual banking system built with Spring Boot, PostgreSQL, Kafka, and Eureka service discovery. The system provides a complete banking platform with user management, account handling, transaction processing, and API gateway integration.

## Features

- **User Management Service** - User registration, authentication, and profile management
- **Account Service** - Account creation, management, and multi-account support
- **Transaction Service** - Real-time transaction processing, history tracking, and transfer management
- **Logging Service** - Centralized logging and audit trail for all banking operations
- **Backend for Frontend (BFF)** - Aggregation layer for seamless client communication
- **API Gateway** - WSO2 API Manager for API management, rate limiting, and security
- **Service Discovery** - Eureka-based service registry for dynamic service location
- **Event-Driven Architecture** - Apache Kafka for asynchronous messaging and event streaming
- **Comprehensive Logging & Monitoring** - Centralized logging to PostgreSQL with Kafbat UI for Kafka monitoring

##  Tech Stack

| Component | Technology |
|-----------|-----------|
| **Framework** | Spring Boot |
| **Language** | Java |
| **Databases** | PostgreSQL (separate DB per service) |
| **Message Broker** | Apache Kafka |
| **Service Discovery** | Eureka (Netflix OSS) |
| **API Gateway** | WSO2 API Manager |
| **Container Orchestration** | Docker Compose |
| **Kafka UI** | Kafbat |
| **Build Tool** | Maven |

##  Prerequisites

- Docker
- Docker Compose
- At least 4GB of available RAM
- Port availability: 5050-5053 (DBs), 8073-8085 (Services), 8243/8280/9443/9763 (WSO2), 9092-9093 (Kafka)

##  Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/yousefwmorsy/virtual-bank-system.git
cd virtual-bank-system
```

### 2. Configure Environment

Copy the example environment file:

```bash
cp .env.example .env
```

The default configuration is suitable for local testing. Adjust database credentials or ports if needed.

### 3. Run with Docker Compose (Production Mode)

The system uses Docker Compose profiles for modular deployment:

#### Complete Setup (Everything)
```bash
docker compose -f docker-compose.prod.yml --profile all up -d
```

#### Infrastructure Only (Databases + Service Registry)
```bash
docker compose -f docker-compose.prod.yml up -d
```

#### Full System (Infrastructure + All Microservices)
```bash
docker compose -f docker-compose.prod.yml --profile services up -d
```

#### With Event Logging (Kafka + Kafbat UI)
```bash
docker compose -f docker-compose.prod.yml --profile logging up -d
```

## Service Architecture

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| **Service Registry** | 8761 | N/A | Eureka service discovery |
| **Account Service** | 8081 | account_db (5050) | Account management and operations |
| **User Service** | 8082 | user_db (5051) | User authentication and profiles |
| **Transaction Service** | 8083 | transaction_db | Transaction processing and history |
| **Logging Service** | 8084 | logging_db | Centralized audit logging |
| **BFF Service** | 8085 | N/A | Backend for Frontend aggregation |
| **WSO2 API Gateway** | 8243/8280/9443/9763 | N/A | API management and rate limiting |

##  Configuration

### Environment Variables

Key environment variables in `.env`:

```bash
# Database Configuration (defaults provided)
DB_USER_HOST=user-db
DB_ACCOUNT_HOST=account-db
DB_TRANSACTION_HOST=transaction-db
DB_LOGGING_HOST=logging-db

# Kafka Bootstrap Servers
KAFKA_BOOTSTRAP_SERVERS=kafka-server:9092

# Service Discovery
EUREKA_HOST=service-registry
EUREKA_PORT=8761

# Server Ports (customize if needed)
SERVER_PORT_ACCOUNT=8080
SERVER_PORT_USER=8088
SERVER_PORT_TRANSACTION=8089
```

Images are pulled from GitHub Container Registry (GHCR). 

## Project Structure

```
VBank-Microservices/
├── services/
│   ├── account-service/        # Account management microservice
│   ├── user-service/           # User authentication & profiles
│   ├── transaction-service/    # Transaction processing
│   ├── logging-service/        # Centralized logging
│   ├── bff-service/            # Backend for Frontend
│   └── service-registry/       # Eureka service discovery
├── wso2-apis/                  # WSO2 API configurations
├── postman-collections/        # API testing collections
├── docs/                       # Architecture, Technical, and API documentation
├── docker-compose.yml          # Development setup
├── docker-compose.prod.yml     # Production setup
├── .env.example                # Environment variables template
└── README.md                   # This file
```

##  Stopping the System
```bash
# Stop all services
docker compose -f docker-compose.prod.yml --profile all down

# Remove volumes (WARNING: deletes data)
docker compose -f docker-compose.prod.yml --profile all down -v
```

##  Documentation

- [Services Documentation](docs/DOCUMENTATION.md) - Complete technical documentation for all services
- [API Reference](./docs/API_REFERENCE.md) - Complete API endpoint documentation
- [Architecture Diagram](./docs/ARCHITECTURE_DIAGRAM.md) - System architecture and component overview
- [Postman Collections](./postman-collections/Virtual%20Bank.postman_collection.json) - Pre-configured API test requests

## Monitoring & Debugging

### Eureka Dashboard
Access the service registry dashboard at: **http://localhost:8761**

Shows all registered microservices, their health status, and availability.

### Kafbat Kafka UI
Access Kafka monitoring at: **http://localhost:8073** (when logging profile is enabled)

Monitor message flows, topics, and consumer groups.

## Troubleshooting

### Ports Already in Use
If ports are already occupied, modify the port mappings in `docker-compose.prod.yml` or `.env`:
```yaml
ports: ["<new_port>:<container_port>"]
```

### Database Connection Issues
Verify database containers are running and healthy:
```bash
docker compose -f docker-compose.prod.yml ps
```

### Out of Memory
If Docker runs out of memory, increase Docker Desktop memory allocation or reduce the number of services.