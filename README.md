# Angular + Spring BFF + Microservices Platform

Enterprise-style reference architecture built as a portfolio project: an Angular SPA never talks to a microservice directly — every request flows through a **Backend-for-Frontend (BFF)**, which is itself a **Spring Cloud Gateway** that either proxies or aggregates calls into four independent Spring Boot microservices.

```
Angular
   |
   v
Frontend BFF (Spring Cloud Gateway, WebFlux, JWT auth, Redis cache)
   |
   v
   +------------+------------+------------+
   |            |            |            |
Customer     Product       Order       Payment
Service      Service      Service      Service
(8081)       (8082)       (8083)       (8084)
```

`order-service` and `payment-service` also talk to each other asynchronously over **Kafka**: creating an order publishes an `OrderCreatedEvent`, `payment-service` consumes it, auto-approves a payment, and publishes a `PaymentProcessedEvent` back so the order flips to `PAID`.

## Technologies

**Backend** — Java 25, Spring Boot 4.0.8, Spring Cloud Gateway (WebFlux), Spring Security + JWT, Spring Data JPA, Spring Kafka, Spring Data Redis (reactive), PostgreSQL, Maven, Docker/Podman.

**Frontend** — Angular 20 (standalone components, signals), TypeScript, Angular Material, RxJS, light/dark theming.

**Infrastructure** — PostgreSQL (one instance, one database per microservice), Redis (BFF read-through cache), Kafka (KRaft mode, no Zookeeper), Docker Compose / Podman Compose.

## Repository layout

```
angular-spring-bff-platform/
├── backend/
│   ├── bff-gateway/        # Spring Cloud Gateway + BFF aggregation + JWT auth (port 8080)
│   ├── customer-service/   # Customer CRUD (port 8081)
│   ├── product-service/    # Product catalog CRUD (port 8082)
│   ├── order-service/      # Order CRUD + Kafka producer/consumer (port 8083)
│   └── payment-service/    # Payment records + Kafka producer/consumer (port 8084)
├── frontend/               # Angular 20 SPA
├── infra/
│   └── init-databases.sql  # creates the 4 per-service databases on first Postgres boot
└── docker-compose.yml
```

## Backend-for-Frontend responsibilities

The gateway is not a dumb proxy. Besides routing (`/gateway/customers/**`, `/gateway/products/**`, `/gateway/orders/**`, `/gateway/payments/**` → the matching microservice), it exposes screen-shaped aggregation endpoints under `/api/bff/**`:

- `GET /api/bff/dashboard` — fans out to all four services in parallel and folds the results into one DTO (KPI counters + recent orders enriched with the customer's name).
- `GET /api/bff/orders/{id}/detail` — joins an order with its customer and product names into a single response, so the Angular order-detail screen needs exactly one HTTP call.
- `POST /api/bff/orders` — validates the customer and every product referenced in the order (calling customer-service and product-service) before delegating the actual creation to order-service.
- Product lookups by id are cached in Redis (cache-aside, 30s TTL) to take read pressure off product-service.

Authentication: `POST /api/auth/login` (demo credentials `admin` / `admin123`) issues an HS256 JWT; every other endpoint requires `Authorization: Bearer <token>`.

## Running locally

### With Docker/Podman Compose (recommended)

```bash
docker compose up --build
# or, with Podman:
podman compose up --build
```

This brings up Postgres, Redis, Kafka, all four microservices, the BFF gateway (`:8080`) and the built Angular app served via Nginx (`:4300`).

### Running each piece by hand

1. Start Postgres, Redis and Kafka (see `docker-compose.yml` for the exact images/config, or run them however you like locally).
2. From each `backend/<service>` directory: `./mvnw spring-boot:run` (or `mvnw.cmd` on Windows).
3. From `frontend`: `npm install && npm start`, then open `http://localhost:4200`.

## Tests

Each backend module ships with unit tests that don't require a live database or broker (`./mvnw test` inside any `backend/<service>` folder). The frontend has its own test setup under `frontend/`.
