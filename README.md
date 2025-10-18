# RAG Backend Platform
A production-ready backend microservices for securely storing and managing RAG-based chatbot conversations, built with Java 21, Spring Boot, PostgreSQL, Redis, and Dockerized microservice architecture.

## Tech Stack

- Java 21
- Spring Boot 3.5
- PostgreSQL
- Redis
- Spring Data JPA
- Spring Security (API Key)
- Spring Cache
- Rate Limiting
- Springdoc OpenAPI
- Docker/Compose
- ELK (Logstash/Elasticsearch/Kibana)
- OpenTelemetry
- Spring Cloud Config
- Spring Cloud Gateway

## Features

| #  | Feature                                       | Description                                                                                      |
| -- | --------------------------------------------- | ------------------------------------------------------------------------------------------------ |
| 1  | **Microservice Architecture**                 | Built with a modular design using Spring Boot microservices for scalability and maintainability. |
| 2  | **Chat Conversation Management**              | Complete CRUD operations for chat sessions and messages, including rename, favorite, and delete. |
| 3  | **Spring Cloud Gateway & Eureka Discovery**   | API Gateway for routing and service discovery across microservices.                              |
| 4  | **Redis Caching**                             | Used for caching chat sessions, messages, and supporting rate limiting for better performance.   |
| 5  | **PostgreSQL Storage**                        | Reliable persistence for chat sessions and message data.                                         |
| 6  | **API Key Authentication**                    | Secure communication between clients and services using `X-API-Key` header.                      |
| 7  | **Rate Limiting**                             | Prevents abuse and ensures fair usage with in-memory and Redis-based implementations.            |
| 8  | **Centralized Logging (ELK Stack)**           | Logs collected via Logstash → stored in Elasticsearch → visualized in Kibana.                    |
| 9  | **OpenTelemetry Integration**                 | Distributed tracing for monitoring inter-service communication and performance.                  |
| 10 | **Global Exception Handling**                 | Unified error responses through a centralized exception handler.                                 |
| 11 | **Health Checks (Actuator)**                  | Application liveness and readiness endpoints for monitoring.                                     |
| 12 | **Pagination Support**                        | Efficient retrieval of paginated chat messages using Spring Data `PageRequest`.                  |
| 13 | **Swagger/OpenAPI Docs**                      | Auto-generated API documentation for testing and exploration.                                    |
| 14 | **Dockerized Setup**                          | End-to-end Docker Compose environment for local development and testing.                         |
| 15 | **Centralized Configuration (Config Server)** | Spring Cloud Config Server for shared configuration across services.                             |
| 16 | **Environment-Specific Configs**              | `.env.local`, `.env.dev`, `.env.prod` for environment isolation and runtime configuration.       |
| 17 | **Unit & Integration Tests**                  | Core functionalities tested using `spring-boot-starter-test`.                                    |
| 18 | **Auditing & Logging**                        | Audit trail maintained for session/message creation, updates, and deletions.                     |
| 19 | **DTO Mapping with MapStruct**                | Clean transformation between entities and DTOs for API responses.                                |
| 20 | **Security-First Design**                     | Proper separation of internal/external APIs and protection of sensitive endpoints.               |
| 21 | **Observability Ready**                       | Metrics, traces, and logs integrated via OTEL and ELK.                                           |
| 22 | **CI/CD Ready**                               | Modular structure supports independent builds, containerization, and deployment pipelines.       |

## Microservices

| Module                | Description                                |
|-----------------------|--------------------------------------------|
| `chatstorage-service` | Manages the RAG-base chatbot conversations |
| `config-server`       | Externalized configuration provider        |
| `discovery-service`   | Eureka-based service registry              |
| `api-gateway-service` | API Gateway and route handler              |
| `common-service`      | Shared DTOs, exceptions, and utilities     |

## Repository Layout (key modules)

```rag-backend-platform/
│├── chatstorage-service/       # Chat storage microservice
│├── config-server/             # Spring Cloud Config Server
│├── discovery-service/         # Eureka Service Discovery
│├── api-gateway-service/       # API Gateway with routing and rate limiting
│├── common-service/            # Shared DTOs, exceptions, utilities
│├── docker/                    # Docker Compose files and related configs
│├── config-repository/         # Config Server Git repository
│└── README.md                  # This documentation
```

## Requirements

- JDK 21 
- Docker & Docker Compose 
- Maven 3.9+

## Quick Start (Local, everything via Docker)

The compose files live in ./docker. We use .env.* files to drive all settings.

### 1. Create .env files (examples)
- docker/.env.local (develop locally, Config Server in git mode)
- docker/.env.dev (development environment, Config Server in git mode)
- docker/.env.prod (production environment, Config Server in git mode)

### 2. Local Development

Build the entire project, after cloning the repo:

```bash
  cd rag-backend-platform
  mvn clean install
```

## Running locally

1. Prepare infra - Bring the required setup via Docker - PostgreSQL, Redis, ELK, Config Server

```bash
  cd docker
  docker compose --env-file .env.local -f docker-compose.rag.base.yml up --build -d
```

2. Start all services with all infra:

```bash
  docker compose --env-file .env.local -f docker-compose.rag.base.yml -f docker-compose.rag.backend.services.yml up --build -d
```

3. Stop all services:

```bash
   docker compose --env-file .env.local -f docker-compose.rag.base.yml -f docker-compose.rag.backend.services.yml down -v --remove-orphans
```

4. To check logs of a particular service:

```bash
  docker compose --env-file .env.local -f docker-compose.rag.base.yml -f docker-compose.rag.backend.services.yml logs -f chatstorage-service
```

> Note: Ensure Docker is running and ports mentioned in the docker-compose.*.yml are available.

## REST API Endpoints (chatstorage-service)
- Base path (service): /
- Base path (via gateway): /ragchatstorage/api/**

### Sessions
- `POST /sessions` → Create new chat session
- `GET /sessions` → Get all chat sessions
- `GET /sessions/{sessionId}` → Get chat session by ID
- `PATCH /sessions/{sessionId}` → Rename chat session
- `PATCH /sessions/{sessionId}?favorite=true` → Mark/unmark chat session as favorite
- `DELETE /sessions/{sessionId}` → Delete chat session by ID

### Messages
- `POST /sessions/{sessionId}/messages` → Add new message to session
- `GET /sessions/{sessionId}/messages?page={page}&size={size}` → Get messages for session
- `GET /sessions/{sessionId}/messages` → Get messages by session ID
- `DELETE /sessions/{sessionId}/messages/{messageId}` → Delete message by ID
- `DELETE /sessions/{sessionId}/messages` → Delete all messages in session

## Health checks
- Config Server: http://localhost:8888/actuator/health, http://localhost:8888/chatstorage-service/local
- Chat Storage: http://localhost:8081/ragchatstorage/actuator/health
- API Gateway: http://localhost:8080/actuator/health
- pgAdmin: http://localhost:5050/

## Observability
- Logs: Logback → Logstash (LOGSTASH_HOST:LOGSTASH_PORT) → Elasticsearch → Kibana (http://localhost:5601/)
  - Local default goes to localhost:5001 
  - Dev/Prod containers use logstash:5001 on the same Docker network
- Traces & Metrics: OTLP exporter to otel-collector (http://localhost:4318/v1/traces)
- Access Eureka **Service Discovery**: http://localhost:8761

## Swagger / API Docs
- Through service directly: http://localhost:8081/ragchatstorage/swagger-ui/index.html
- Through Gateway (if you added swagger routes): http://localhost:8080/chatstorage-service/ragchatstorage/swagger-ui/index.html

> Note: Authorize and provide your API key in header X-API-Key in Swagger UI to test endpoints.

## Security (API Key)
- All endpoints (except /actuator/**) require header:
`X-API-Key: <value from environment API_KEY>`
- In local: API_KEY= local-ragchat-api-key 
- In dev/prod: set securely per environment

## CORS (example):
- Allowed origin: http://localhost:5173
- Allowed headers: Content-Type, X-API-Key

## Configurations
- Central config repo: [config-repository/](https://github.com/deepa-ganesh/rag-backend-platform/tree/main/config-repository)
- Swagger/OpenAPI: enabled via SpringDoc in each service
- OpenTelemetry Collector config: [docker/otelcol/docker/otel-collector-config.yml](https://github.com/deepa-ganesh/rag-backend-platform/blob/main/docker/otelcol/docker/otel-collector-config.yml)
- Logstash config: [docker/logstash/docker/](https://github.com/deepa-ganesh/rag-backend-platform/tree/main/docker/logstash/docker)


### Caching (Redis)
- Caches 
  - sessionsList → cached list of sessions 
  - sessions → individual session details 
  - sessionMessages → message lists per (sessionId, page, size) tuple
- TTL configurable via CACHE_TTL (seconds).

**Notes**:

- We rely on @CacheEvict on mutating operations (create/rename/delete) to keep caches consistent.

## Rate Limiting
This project implements two types of rate limiting using Redis:

1. Spring Cloud Gateway – Built-in Redis Rate Limiter
    - Location: api-gateway-service
    - Description: Uses Spring Cloud Gateway’s built-in RedisRateLimiter to apply rate limits at the gateway layer, ensuring all incoming traffic is controlled before reaching internal microservices.
2. Custom Redis Rate Limiter
    - Location: chatstorage-service
    - Local profile → In-memory (ConcurrentHashMap + sliding window)
    - Dev/Prod → Redis atomic operations (keyed by clientId/IP)
    - Controlled by:
      - RATE_LIMIT_REQUESTS → allowed requests per window 
      - RATE_LIMIT_TIME_WINDOW → window seconds

## Error Handling
- Centralized GlobalExceptionHandler returns clear JSON errors with appropriate HTTP status:
  - 400 → validation errors (@Valid, @NotBlank, etc.)
  - 404 → not found (session/message ids)
  - 429 → rate-limit exceeded 
  - 500 → unhandled errors (with correlation IDs in logs)

## Further Enhancements
| Area                         | Description                                                                   |
| ---------------------------- | ----------------------------------------------------------------------------- |
| **Authentication & Roles**   | Add JWT-based authentication with role-based access (Admin, User, Service).   |
| **Event Integration**        | Introduce Kafka or RabbitMQ for asynchronous message events and auditing.     |
| **Search Functionality**     | Implement full-text search of conversations using Elasticsearch.              |
| **Async Processing**         | Use `@Async` or message queues to handle long-running tasks.                  |
| **Enhanced Rate Limiting**   | Add user-based quotas and configurable thresholds per environment.            |
| **Data Archival**            | Auto-archive or delete old chat sessions after a set retention period.        |
| **Monitoring Dashboard**     | Add Prometheus + Grafana for real-time metrics visualization.                 |
| **Unit & Integration Tests** | Expand test coverage for services, controllers, and caching logic.            |
