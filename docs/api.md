# API Documentation

## Interactive Documentation

The service exposes a Swagger UI powered by [springdoc-openapi](https://springdoc.org).

| Resource | URL |
|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON spec | `http://localhost:8080/v3/api-docs` |

Swagger UI does not require an API key to view. To execute requests from the UI, click the **Authorize** button and enter your API key.

---

## Authentication

All endpoints require an API key passed in the `X-Api-Key` header.

```
X-Api-Key: your-secret-api-key
```

The key is configured via the `API_KEY` environment variable (default: `changeme`). Requests missing or with a wrong key receive `401 Unauthorized`.

The Swagger UI and OpenAPI spec endpoints (`/swagger-ui/**`, `/v3/api-docs/**`) are public — no key required.

---

## Endpoints

### Coupons — `POST /coupon`

Creates a new coupon.

**Request body:**
```json
{
  "code": "SUMMER20",
  "usageLimit": 100,
  "countryCodes": ["PL", "DE", "FR"]
}
```

| Status | Condition |
|---|---|
| `201 Created` | Coupon created. `Location` header points to `/coupon/{id}` |
| `400 Bad Request` | Validation failed, invalid code format, or invalid usage limit |
| `404 Not Found` | One or more country codes do not exist |
| `409 Conflict` | Coupon with this code already exists |

---

### Coupons — `GET /coupon/{id}`

Returns coupon details by ID.

| Status | Condition |
|---|---|
| `200 OK` | Coupon found |
| `404 Not Found` | Coupon not found |

---

### Coupons — `GET /coupon`

Returns a paginated list of coupons.

**Query parameters:**

| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | int | `0` | Page number (0-based) |
| `size` | int | — | Page size (1–100) |
| `search` | string | — | Filter by code (partial match) |
| `createdAtFrom` | ISO 8601 | — | Created from (inclusive) |
| `createdAtTo` | ISO 8601 | — | Created until (inclusive) |

| Status | Condition |
|---|---|
| `200 OK` | Paginated result with navigation links |
| `400 Bad Request` | Invalid query parameters |

---

### Coupons — `DELETE /coupon/{id}`

Deletes a coupon by ID.

| Status | Condition |
|---|---|
| `204 No Content` | Coupon deleted |
| `404 Not Found` | Coupon not found |

---

### Coupon Redemptions — `POST /coupon-redemption`

Redeems a coupon for a user from an external system. The caller's IP address is used to determine the country.

**Request body:**
```json
{
  "couponCode": "SUMMER20",
  "externalUser": "user-123",
  "externalSystem": "my-app"
}
```

| Status | Condition |
|---|---|
| `201 Created` | Coupon redeemed successfully |
| `400 Bad Request` | Invalid request body |
| `403 Forbidden` | Coupon not eligible for the caller's country |
| `404 Not Found` | Coupon not found |
| `409 Conflict` | Coupon exhausted or already redeemed by this user |
| `422 Unprocessable Entity` | Caller's IP address could not be resolved to a country |

---

## Error Response Format

All error responses follow [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457) (`application/problem+json`):

```json
{
  "type": "about:blank",
  "title": "Coupon exhausted",
  "status": 409,
  "detail": "Coupon SUMMER20 has reached its usage limit.",
  "instance": "/coupon-redemption"
}
```
