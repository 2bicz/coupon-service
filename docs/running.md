# Running the Service

## Prerequisites

- Docker and Docker Compose
- MaxMind account (free) — required for IP geolocation

## 1. MaxMind GeoLite2 Setup

The service uses the MaxMind GeoLite2-Country offline database to resolve IP addresses to country codes. MaxMind distributes it for free but requires registration.

1. Create a free account at [maxmind.com](https://www.maxmind.com/en/home). You can find out how to do it in [this section of "Create a MaxMind account" article](https://support.maxmind.com/knowledge-base/articles/create-a-maxmind-account#sign-up-for-geolite).
2. After logging in, go to **Account → Manage License Keys → Generate new license key**
3. Note your **Account ID** (visible in the top-right corner of the account page) and the generated **License Key**

## 2. Environment Configuration

Create a `.env` file in the project root. Docker Compose reads it automatically.

```dotenv
# PostgreSQL
DB_NAME=coupon_service
DB_USER=user
DB_PASSWORD=changeme
DB_EXTERNAL_PORT=5432        # host port for postgres (optional, default: 5432)

# MaxMind GeoIP
GEOIPUPDATE_ACCOUNT_ID=your_account_id
GEOIPUPDATE_LICENSE_KEY=your_license_key

# Application
API_KEY=your-secret-api-key
APP_EXTERNAL_PORT=8080       # host port for the app (optional, default: 8080)
SERVER_PORT=8080             # port inside the container (optional, default: 8080)
```

## 3. Production — Full Stack with Docker Compose

`docker-compose.yml` runs the complete stack: PostgreSQL, `geoipupdate`, and the application.

The `geoipupdate` service downloads and periodically refreshes the GeoLite2-Country database (every 168 hours). It is stored in a named Docker volume shared with the application container.

```bash
docker compose up --build
```

On first startup, `geoipupdate` downloads the database before the application can serve geolocation-dependent requests. The application itself starts immediately but will return `422` for any redemption request until the database file is present.

To run in the background:

```bash
docker compose up --build -d
```

To stop:

```bash
docker compose down
```

## 4. Local Development — Infrastructure Only

`docker-compose.local.yml` runs only PostgreSQL and `geoipupdate` — no application container. Use this when running the application directly on your machine (IDE, `mvnw`).

The GeoLite2-Country database is written to `./data/geoip/` on your host (bind mount), so the locally running application can access it directly.

```bash
docker compose -f docker-compose.local.yml up -d
```

Then start the application with:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=coupon_service
export DB_USER=user
export DB_PASSWORD=changeme
export GEOLOCATION_DB_PATH=./data/geoip/GeoLite2-Country.mmdb
export API_KEY=dev-key

./mvnw spring-boot:run
```

Wait for `geoipupdate` to finish the initial download before sending redemption requests. The `./data/geoip/` directory is git-ignored.

To stop:

```bash
docker compose -f docker-compose.local.yml down
```

## 5. Testing Coupon Redemption Locally

The redemption endpoint (`POST /coupon-redemption`) resolves the caller's IP address to a country code using the MaxMind GeoLite2 database. This lookup is required before a coupon can be redeemed.

When sending requests locally (from Postman, curl, or any tool on your machine), the IP seen by the application is the loopback address — `127.0.0.1` or `::1` (IPv6). MaxMind's database contains only public IP ranges and has no record for loopback addresses, so every redemption attempt returns `422 Unprocessable Entity` with `"IP address not resolvable"`.

**Fix:** add an `X-Forwarded-For` header with any public IP address. The application reads this header first (see `IpExtractor`) and passes its value to geolocation instead of the raw remote address.

```
X-Forwarded-For: 8.8.8.8
```

Any routable public IP works. To test country-restricted coupons, pick an IP that resolves to the target country — for example `185.220.101.1` for Germany. Free tools such as [ip-api.com](https://ip-api.com) can help find IPs by country.

> This applies equally to local bare-metal runs and local Docker Compose — the loopback problem is the same in both cases.

## Environment Variable Reference

| Variable | Required | Default | Description |
|---|---|---|---|
| `DB_HOST` | No | `localhost` | PostgreSQL host |
| `DB_PORT` | No | `5432` | PostgreSQL port |
| `DB_NAME` | Yes | — | Database name |
| `DB_USER` | Yes | — | Database user |
| `DB_PASSWORD` | Yes | — | Database password |
| `GEOLOCATION_DB_PATH` | No | `/app/data/GeoLite2-Country.mmdb` | Path to MaxMind .mmdb file |
| `GEOIPUPDATE_ACCOUNT_ID` | Yes (Docker) | — | MaxMind account ID |
| `GEOIPUPDATE_LICENSE_KEY` | Yes (Docker) | — | MaxMind license key |
| `API_KEY` | No | `changeme` | API key for `X-Api-Key` header |
| `SERVER_PORT` | No | `8080` | Application port |