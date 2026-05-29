# Architecture

## Overview

The service follows **Hexagonal Architecture** (Ports & Adapters) combined with **CQRS**.

```
adapter/in/web          ← HTTP entry points (Spring MVC controllers)
application/port/in     ← Use case interfaces (inbound ports)
application/port/out    ← Repository / external service interfaces (outbound ports)
application/service     ← Use case implementations
domain/                 ← Pure business logic — no framework dependencies
adapter/out/persistence ← JPA adapters implementing outbound ports
adapter/out/geolocation ← MaxMind adapter implementing IpGeolocationPort
```

The domain layer has zero dependency on Spring, JPA, or any other framework. Business rules live in `Coupon` and are enforced before any persistence call.

CQRS is applied at the persistence level: write operations use the `coupon` table directly, while reads go through the `coupon_view` database view that pre-aggregates usage counts and country codes.

---

## Key Architectural Challenges

### 1. Race Conditions in Coupon Redemption

**Problem:** Multiple concurrent requests can redeem the same coupon simultaneously. Without coordination, the `usageLimit` can be exceeded — two threads may both read `usageCount = 9` on a limit-10 coupon and both proceed.

**Solution chosen: Pessimistic locking (`SELECT FOR UPDATE`)**

The coupon row is locked at the database level when fetched. All concurrent redemption requests for the same coupon are serialized. The usage count is read and validated inside the same transaction, preventing any other transaction from reading a stale count.

See `CouponPersistenceAdapter.getByCodeWithLock()` and `CouponRedemptionService.redeem()`.

**Alternatives considered:**

| Alternative | How it works | Why not chosen |
|---|---|---|
| Optimistic locking (`@Version`) | Read freely, detect conflict on write, retry | High-contention coupons (flash sales) cause many retries and degrade throughput |
| DB CHECK constraint + trigger | Enforce `usageCount <= usageLimit` at DB level | Complex to implement correctly; requires trigger to maintain count |
| Distributed lock (Redis) | Acquire named lock before processing | Adds infrastructure dependency; DB lock is sufficient when a single DB is used |

Pessimistic locking is the right default for this domain: coupon redemption is inherently a critical section, contention is expected, and correctness matters more than throughput.

---

### 2. External User Identity — Find-or-Create Under Concurrency

**Problem:** The service maps external users (from other systems) to internal UUIDs. On first encounter a record is created. Two concurrent requests for the same external user (e.g. redeeming different coupons simultaneously) can both attempt to insert the same record, causing a unique constraint violation.

**Solution chosen: Optimistic upsert with constraint-violation retry + `REQUIRES_NEW` transaction**

`ExternalPartyPersistenceAdapter.findOrCreateExternalUserId` tries to find, then insert. On `DataIntegrityViolationException` it falls back to a second find. The method runs in `REQUIRES_NEW` so the constraint violation does not mark the outer transaction as rollback-only.

**Alternatives considered:**

| Alternative | How it works | Why not chosen |
|---|---|---|
| Native `INSERT ... ON CONFLICT DO NOTHING RETURNING *` | Atomic upsert in one SQL statement | Requires native query, leaks SQL dialect into the adapter |
| Pre-registration flow | External users must be registered before redeeming | Changes the API contract; more client-side complexity |
| Synchronized block / distributed lock | Serialize first-time creation | Overkill for a rare event; hurts performance for already-existing users |

---

### 3. Geolocation-Based Country Eligibility

**Problem:** Coupons are restricted to specific countries. The country must be determined server-side — the client cannot be trusted to declare its own location.

**Solution chosen: MaxMind GeoLite2 offline database**

The caller's IP address is resolved to a country code using a local MaxMind `.mmdb` file. No HTTP call is made — the lookup is an in-process file read, adding sub-millisecond latency. The database is refreshed automatically every 7 days via `geoipupdate`.

**Alternatives considered:**

| Alternative | How it works | Why not chosen |
|---|---|---|
| Client-declared country | Client sends country in request body | Trivially spoofable; cannot be used for access control |
| CDN geolocation header (e.g. `CF-IPCountry`) | CDN injects country before request reaches the service | Ties the service to a specific CDN; can be bypassed if the service is exposed directly |
| External geolocation HTTP API | HTTP call to ip-api.com or similar per request | Adds network latency and an external point of failure; unsuitable for high-traffic use |

---

### 4. CQRS — Separate Read and Write Models

**Problem:** Fetching a coupon for display requires joining `coupon`, `coupon_redemption` (for the count), and `coupon_country`. Doing this in the write model every time is verbose and couples reads to the write schema.

**Solution chosen: `coupon_view` database view**

A PostgreSQL view pre-joins the three tables and exposes `usage_count` and `country_codes` as aggregated columns. Read queries go through `CouponViewEntity` (mapped to the view, annotated `@Immutable`) while write operations use `CouponEntity` directly.

This means the read model can evolve independently of the write model and adding new fields to the view does not touch the write path.
