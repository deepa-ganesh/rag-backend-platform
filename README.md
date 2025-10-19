# RAG Backend Platform
A production-ready backend microservices for securely storing and managing RAG-based chatbot conversations, built with Java 21, Spring Boot, PostgreSQL, Redis, and Dockerized microservice architecture.

## Requirements

- JDK 21
- Docker & Docker Compose
- Maven 3.9+

## Quick Start (Local, everything via Docker)

The compose files live in ./docker. We use .env.* files to drive all settings.

### Build the project

```bash
  cd rag-backend-platform
  mvn clean install
```

### Running locally
1. Create .env files (examples)
- `docker/.env.local` (develop locally, Config Server in git mode)
- `docker/.env.dev` (development environment, Config Server in git mode)
- `docker/.env.prod` (production environment, Config Server in git mode)

2. Prepare infra - Bring the required setup via Docker - PostgreSQL, Redis, ELK, Config Server

```bash
  cd docker
  docker compose --env-file .env.local -f docker-compose.rag.base.yml up --build -d
```

3. Start all services:

**Option 1: Local**:
```bash
  cd discovery-service
  mvn spring-boot:run -Dspring-boot.run.profiles=local
  
  cd config-server
  mvn spring-boot:run -Dspring-boot.run.profiles=local
  
  cd chatstorage-service
  mvn spring-boot:run -Dspring-boot.run.profiles=local
  
  cd api-gateway-service
  mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Option 2: Docker**:
```bash
  docker compose --env-file .env.local -f docker-compose.rag.base.yml -f docker-compose.rag.backend.services.yml up --build -d
```

4. To check logs of a particular service:

```bash
  docker compose --env-file .env.local -f docker-compose.rag.base.yml -f docker-compose.rag.backend.services.yml logs -f chatstorage-service
```

*Bonus*: Stop all services:

```bash
   docker compose --env-file .env.local -f docker-compose.rag.base.yml -f docker-compose.rag.backend.services.yml down -v --remove-orphans
   docker system prune -af --volumes
```

> Note: Ensure Docker is running and ports mentioned in the docker-compose.*.yml are available.

## Verify services
- Config Server: http://localhost:8888/actuator/health
- Config Server Properties: http://localhost:8888/chatstorage-service/local
- Chat Storage: http://localhost:8081/ragchatstorage/actuator/health
- API Gateway: http://localhost:8080/actuator/health
- Access Eureka **Service Discovery**: http://localhost:8761
- Centralized Logs: **Logback → Logstash → Elasticsearch → Kibana**: http://localhost:5601/
- pgAdmin: http://localhost:5050/

## Swagger / API Docs
- Through Service: http://localhost:8081/ragchatstorage/swagger-ui/index.html
- Through Gateway: http://localhost:8080/chatstorage-service/ragchatstorage/swagger-ui/index.html

## Tech Stack
- Java 21
- Spring Boot 3.5
- PostgreSQL
- Redis
- Springdoc OpenAPI
- Docker/Compose
- ELK (Logstash/Elasticsearch/Kibana)
- OpenTelemetry
- Spring Cloud Config
- Spring Cloud Gateway

## Features
- Microservice architecture with Spring Boot
- Chat conversation management (CRUD for sessions/messages)
- API Gateway with routing and rate limiting
- Redis caching for performance
- PostgreSQL for reliable storage
- API Key authentication for security
- Centralized logging with ELK stack
- OpenTelemetry for distributed tracing
- Health checks with Spring Boot Actuator
- Pagination support for message retrieval
- Swagger/OpenAPI documentation
- Dockerized setup for easy deployment
- Centralized configuration with Spring Cloud Config
- Environment-specific configurations
- Unit and integration tests
- Auditing and logging of operations
- Global exception handling

## Microservices
| Module                | Description                                |
|-----------------------|--------------------------------------------|
| `chatstorage-service` | Manages the RAG-base chatbot conversations |
| `config-server`       | Externalized configuration provider        |
| `discovery-service`   | Eureka-based service registry              |
| `api-gateway-service` | API Gateway and route handler              |
| `common-service`      | Shared DTOs, exceptions, and utilities     |

## Key Modules
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

## REST API Key Endpoints (chatstorage-service)
- Base path (service): /api/**
- Base path (via gateway): /ragchatstorage/api/**

### Sessions
- `POST /api/sessions/` → Create new chat session
- `GET /api/sessions/` → Get all chat sessions
- `GET /api/sessions/{sessionId}` → Get chat session by ID
- `PATCH /api/sessions/{sessionId}/rename` → Rename chat session
- `PATCH /api/sessions/{sessionId}/favorite?favorite=true` → Mark or unmark chat session as favorite
- `DELETE /api/sessions/{sessionId}` → Delete chat session by ID

- `POST /api/sessions/{sessionId}/messages` → Add new message (supports optional retrieved context)
- `GET /api/sessions/{sessionId}/messages?page={page}&size={size}` → Get messages for session (paginated)
- `DELETE /api/sessions/{sessionId}/messages` → Delete all messages in a session
- `DELETE /api/messages/{messageId}` → Delete a single message by ID

> Note: Authorize and provide your API key in header X-API-Key in Swagger UI to test endpoints.

## Security (API Key & Filters)
- All endpoints (except /actuator/** and Swagger docs) require header `X-API-Key: <value from environment API_KEY>`.
- Local default: `API_KEY=local-ragchat-api-key`; override per environment in `.env.*`.
- Servlet filter chain enforces security:
  - `BaseFilter` defines a single allowlist for public paths (health checks, Swagger).
  - `AuthenticationFilter` (order 1) validates the API key.
  - `RateLimitFilter` (order 2) applies service-level throttling.
  - `AuditFilter` (order 3) populates correlation/audit context.
- Spring Security is kept minimal — CSRF is disabled and the custom filters handle authentication and authorization logic.

## Configurations
- Central config repo: [config-repository/](https://github.com/deepa-ganesh/rag-backend-platform/tree/main/config-repository)
  - Chat Storage profiles: `chatstorage-service-{local,dev,prod}.yml`
  - API Gateway profiles: `api-gateway-service-{local,dev,prod}.yml`
- OpenTelemetry Collector config: [docker/otelcol/docker/otel-collector-config.yml](https://github.com/deepa-ganesh/rag-backend-platform/blob/main/docker/otelcol/docker/otel-collector-config.yml)
- Logstash config: [docker/logstash/docker/](https://github.com/deepa-ganesh/rag-backend-platform/tree/main/docker/logstash/docker)

## Caching (Redis - Spring Cache Abstraction)
- Caches 
  - sessionsList → cached list of sessions 
  - sessions → individual session details 
  - sessionMessages → message lists per (sessionId, page, size) tuple
- TTL configurable via CACHE_TTL (seconds).

**Note**:
- We use Spring Cache Abstraction with RedisCacheManager. This can be easily swapped out for another cache provider if needed.
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
| Area                          | Description                                                                   |
|-------------------------------| ----------------------------------------------------------------------------- |
| **Authentication & Roles**    | Add JWT-based authentication with role-based access (Admin, User, Service).   |
| **Event Integration**         | Introduce Kafka or RabbitMQ for asynchronous message events and auditing.     |
| **Enhanced Caching**          | Implement cache warming and distributed cache invalidation strategies.        |
| **Search Functionality**      | Implement full-text search of conversations using Elasticsearch.              |
| **Async Processing**          | Use `@Async` or message queues to handle long-running tasks.                  |
| **Enhanced Rate Limiting**    | Add user-based quotas and configurable thresholds per environment.            |
| **Data Archival**             | Auto-archive or delete old chat sessions after a set retention period.        |
| **Monitoring Dashboard**      | Add Prometheus + Grafana for real-time metrics visualization.                 |
| **Unit & Integration Tests**  | Expand test coverage for services, controllers, and caching logic.            |
## Smoke Test (curl)
- Create a session:
```bash
curl -X POST http://localhost:8081/ragchatstorage/api/sessions/ \
  -H 'Content-Type: application/json' \
  -H 'X-API-Key: local-ragchat-api-key' \
  -d '{"name":"Demo Session"}'
```
- Add a message (note the optional `context` field):
```bash
curl -X POST http://localhost:8081/ragchatstorage/api/sessions/{sessionId}/messages \
  -H 'Content-Type: application/json' \
  -H 'X-API-Key: local-ragchat-api-key' \
  -d '{"sender":"USER","content":"Hello","context":"Weather forecast snippet"}'
```
- List messages with pagination:
```bash
curl -H 'X-API-Key: local-ragchat-api-key' \
  "http://localhost:8081/ragchatstorage/api/sessions/{sessionId}/messages?page=0&size=20"
```

## Troubleshooting
- **Kibana 5601 not responding**: Verify Elasticsearch is healthy via `docker compose logs elasticsearch`. If you see `vm.max_map_count` warnings, set `sudo sysctl -w vm.max_map_count=262144` on the host and restart the stack so Elasticsearch can allocate memory.
- **Gateway container unhealthy**: Health checks now rely on Redis being marked `healthy` and on successfully fetching configuration from Config Server/Eureka. If the gateway keeps restarting, inspect `docker compose logs api-gateway-service` and confirm `redis` reports `healthy`.
- **Elasticsearch exits with code 137**: Docker killed the process due to memory pressure. The default heap has been capped via `ES_JAVA_OPTS`; if the issue persists, increase Docker Desktop’s memory allowance or lower the heap further (e.g., `ES_JAVA_OPTS=-Xms256m -Xmx256m` in your `.env.*`) and remove stale data with `docker compose down -v esdata` before restarting.
- **Logstash exits unexpectedly**: The image defaults to a 1 GB JVM heap. Set `LS_JAVA_OPTS` in your `.env.*` (already wired to 256 MB locally) to fit within Docker Desktop’s memory budget, or allocate more RAM to Docker.
