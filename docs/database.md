# Database Schema

Managed by Liquibase. Changelogs live in `src/main/resources/db/changelog/`.

See also: [diagrams/database-schema.puml](diagrams/database-schema.puml)

---

## Tables

### `external_system`

Represents an external application that integrates with this service. Each system is identified by a `client_id`.

| Column | Type | Notes |
|---|---|---|
| `id` | uuid | PK, auto-generated |
| `client_id` | varchar(100) | Unique. Used as the system identifier in requests |

---

### `external_user`

A user from an external system. Identified by the combination of `external_system_id` and the original user ID from that system (`origin_user_id`).

| Column | Type | Notes |
|---|---|---|
| `id` | uuid | PK, auto-generated |
| `external_system_id` | uuid | FK → `external_system.id` |
| `origin_user_id` | text | The user's ID as known in the external system |

Unique constraint: `(external_system_id, origin_user_id)`.

---

### `country`

Reference table of valid ISO 3166-1 alpha-2 country codes. Seeded at startup via Liquibase. Used to validate country codes on coupon creation.

| Column | Type | Notes |
|---|---|---|
| `code` | varchar(2) | PK. ISO 3166-1 alpha-2 (e.g. `PL`, `DE`) |

---

### `coupon`

The main write-side entity. Stores the coupon definition.

| Column | Type | Notes |
|---|---|---|
| `id` | uuid | PK, auto-generated |
| `code` | varchar(255) | Unique, case-insensitive (stored uppercase) |
| `usage_limit` | int | Maximum number of redemptions |
| `created_at` | timestamptz | Auto-set on insert |

Index: `idx_coupon_code_upper` on `UPPER(code)` — supports case-insensitive lookups.

---

### `coupon_country`

Join table linking coupons to the countries they are valid in.

| Column | Type | Notes |
|---|---|---|
| `coupon_id` | uuid | FK → `coupon.id` |
| `country_code` | char(2) | FK → `country.code` |

PK: `(coupon_id, country_code)`.

---

### `coupon_redemption`

Records each coupon redemption event. One row per user per coupon.

| Column | Type | Notes |
|---|---|---|
| `id` | uuid | PK, auto-generated |
| `coupon_id` | uuid | FK → `coupon.id` |
| `external_user_id` | uuid | FK → `external_user.id` |
| `created_at` | timestamptz | Auto-set on insert |

Unique constraint: `(coupon_id, external_user_id)` — one redemption per user per coupon.

---

## View

### `coupon_view`

Read-side projection used for all query operations. Aggregates data from `coupon`, `coupon_redemption`, and `coupon_country` in a single query.

| Column | Type | Description |
|---|---|---|
| `id` | uuid | Coupon ID |
| `code` | varchar | Coupon code |
| `usage_limit` | int | Maximum redemptions |
| `usage_count` | bigint | Current number of redemptions |
| `created_at` | timestamptz | Creation timestamp |
| `country_codes` | text[] | Array of eligible country codes |

Mapped as a read-only JPA entity (`@Immutable`) in `CouponViewEntity`. Write operations always go through the `coupon` table directly.
