# Running the Service

## Prerequisites

- Docker and Docker Compose
- MaxMind account (free) — required for IP geolocation

## 1. MaxMind GeoLite2 Setup

The service uses the MaxMind GeoLite2-Country offline database to resolve IP addresses to country codes. MaxMind distributes it for free but requires registration.

1. Create a free account at [maxmind.com](https://www.maxmind.com/en/geolite2/signup)
2. After logging in, go to **Account → Manage License Keys → Generate new license key**
3. Note your **Account ID** (visible in the top-right corner of the account page) and the generated **License Key**

The `geoipupdate` service in `docker-compose.yml` uses these credentials to automatically download and periodically refresh the database (every 168 hours). The database is stored in a shared Docker volume mounted into the application container.

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

## 3. Start with Docker Compose

```bash
docker compose up --build
```

On first startup, `geoipupdate` downloads the GeoLite2-Country database before the application can serve geolocation-dependent requests. The application itself starts immediately but will return `422` for any redemption request until the database file is present.

To run in the background:

```bash
docker compose up --build -d
```

To stop:

```bash
docker compose down
```

## 4. Local Development (without Docker)

Requires Java 25 and a running PostgreSQL instance.

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=coupon_service
export DB_USER=user
export DB_PASSWORD=changeme
export GEOLOCATION_DB_PATH=/path/to/GeoLite2-Country.mmdb
export API_KEY=dev-key

./mvnw spring-boot:run
```

Download the database file manually from the MaxMind portal (**Download Databases → GeoLite2 Country → Download → .mmdb**) and set `GEOLOCATION_DB_PATH` to its location.

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
