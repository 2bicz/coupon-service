# Coupon Service

A REST service for managing and redeeming discount coupons. Coupons are restricted by usage limit and eligible countries. Country eligibility is determined server-side via IP geolocation.

## Documentation

| Topic | File                                      |
|---|-------------------------------------------|
| How to run (Docker, MaxMind setup, env vars) | [docs/running.md](docs/running.md)        |
| Architecture, design decisions, trade-offs | [docs/decisions.md](docs/decisions.md) |
| Database schema (tables, view, indexes) | [docs/database.md](docs/database.md)      |
| API reference and Swagger UI | [docs/api.md](docs/api.md)                |

## Diagrams

| Diagram | File |
|---|---|
| Database schema | [docs/diagrams/database-schema.puml](docs/diagrams/database-schema.puml) |
| Coupon creation flow | [docs/diagrams/coupon-creation-flow.puml](docs/diagrams/coupon-creation-flow.puml) |
| Coupon redemption flow | [docs/diagrams/coupon-redemption-flow.puml](docs/diagrams/coupon-redemption-flow.puml) |

## Quick Start

```bash
cp .env.example .env   # fill in DB credentials and MaxMind account details
docker compose up --build
```

See [docs/running.md](docs/running.md) for the full setup guide including MaxMind account creation.

## Tech Stack

- Java 25, Spring Boot 4
- PostgreSQL with Liquibase migrations
- MaxMind GeoLite2 for IP geolocation
- springdoc-openapi (Swagger UI at `/swagger-ui.html`)
