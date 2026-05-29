# Project Decisions

This document describes the most important architectural and design decisions made during the development of the `coupon-service` project, together with the reasoning behind them and the associated trade-offs.

## 1. Ports & Adapters Architecture

The project follows the Ports & Adapters (Hexagonal Architecture) approach.

The main reason for choosing this architecture was flexibility. It allows infrastructure components such as databases or external service providers to be replaced with minimal impact on the domain layer and business logic.

For example, at the beginning of the implementation process, I was not certain which IP geolocation provider should be used for country detection. Thanks to the ports and adapters approach, replacing one provider with another would not require major changes to the application core.

The downside of this approach is increased structural complexity. Compared to a traditional layered architecture (`controller -> service -> repository -> model`), the project contains more classes and navigating through the codebase can initially feel less straightforward.

Despite this additional complexity, I believe the ability to replace infrastructure components at any point in time is a worthwhile long-term investment.

---

## 2. No Coupon Usage Counter Stored in the `coupon` Table

The system allows a coupon to be redeemed only once by a given user. To support this requirement, a separate `coupon_redemption` table was introduced.

This table is connected through a foreign key to the `external_user` table, which stores information about users originating from external systems.

The `coupon_redemption` table contains a unique compound index on the columns:

* `coupon_id`
* `external_user_id`

This guarantees that a specific user from a specific external system can redeem a given coupon only once.

I intentionally decided not to store the current redemption count directly in the `coupon` table.

The reason is that the number of associated `coupon_redemption` records already represents the source of truth for coupon usage. Introducing an additional counter column in the `coupon` table would create a second source of truth that would need to stay synchronized with the actual redemption records.

Maintaining synchronization between these two sources would significantly increase system complexity and make the application harder to maintain. The problem becomes even more complicated in a concurrent environment where multiple clients may redeem coupons simultaneously, introducing potential race conditions.

The main downside of the chosen approach is reduced read performance because the number of redemptions must be calculated dynamically from the `coupon_redemption` table.

Additionally, because I decided to adopt CQRS (described in the next section), I wanted to keep the domain model clean and avoid using read models for business rule validation during redemption operations. As a result, in `CouponRedemptionService` the number of associated `coupon_redemption` records is counted manually during validation.

If the current redemption count had been stored directly on the domain `Coupon` object, this additional query would not have been necessary. However, I preferred preserving a single source of truth over introducing duplicated state.

---

## 3. CQRS

Since the application allows creating and redeeming coupons, it should also provide a convenient way to browse existing coupons.

Because coupon redemption counts are not stored directly in the `coupon` table and coupons can belong to multiple countries through a many-to-many relationship, retrieving coupon data would otherwise require loading:

* the current redemption count,
* and the list of associated countries

for every coupon individually.

This would negatively affect maintainability and increase the likelihood of forgetting one of these data-fetching responsibilities in the future.

To solve this problem, I introduced a dedicated database view that aggregates all read-related data in a single place.

Having separate data models for reads and writes naturally became the starting point for introducing CQRS and separating the write side from the read side of the application.

This separation improves code readability through clearly defined responsibilities and should make future optimization of read and write operations easier depending on evolving business requirements.

The downside is that if the service never grows further, CQRS may ultimately become unnecessary overengineering. However, it is difficult to predict long-term system evolution with complete certainty.

Another important design decision was choosing regular database views instead of materialized views or dedicated read-only tables.

Using materialized views or synchronized read tables would introduce additional complexity related to data synchronization between the write model and the read model. While reads would become faster, the overall system would become significantly harder to maintain.

By using standard database views, every read operation executes the underlying SQL query dynamically. This results in lower read performance compared to materialized views, but eliminates synchronization concerns entirely and keeps the service relatively simple.

Additionally, this approach is naturally safe in concurrent environments.

If performance ever becomes a serious issue in the future, the implementation can still be migrated to materialized views.

---

## 4. Coupons Cannot Be Deleted After Redemption

I decided that once a coupon has been redeemed, its data should remain stored in the system.

Historical redemption data can be highly valuable for analytics and business intelligence purposes. If deletion had been allowed from the very beginning, important historical information could be permanently lost.

Keeping the data available in the database also makes it possible to introduce archival mechanisms in the future if necessary.

---

## 5. Pessimistic Locking During Coupon Redemption Creation

The coupon redemption flow uses pessimistic locking.

In a concurrent environment, multiple requests may attempt to redeem the same coupon at the same time. Without proper coordination, it would be possible for the coupon `usageLimit` to be exceeded because several threads could validate the limit simultaneously before inserting redemption records.

Pessimistic locking guarantees that concurrent redemption attempts are properly serialized and that the usage limit cannot be exceeded due to race conditions.

---

## 6. MaxMind GeoLite2 Offline Database Instead of External HTTP API

The application uses the MaxMind GeoLite2 offline database for IP-to-country resolution.

This solution allows country detection without making external HTTP requests. The application simply reads from a local database file and obtains the ISO country code, which is also used internally for assigning countries to coupons.

A separate Docker container running alongside the application updates the GeoLite2 database once per week.

Compared to using an external HTTP API, this approach offers several advantages:

* lower latency,
* no network dependency during request processing,
* reduced reliance on external providers,
* improved resilience.

If an external API became unavailable, coupon redemption could stop working entirely. In contrast, with the offline database approach, the redemption flow continues to function even if the update service temporarily fails.

In the worst-case scenario, the geolocation database may become slightly outdated, but the application itself remains fully operational.
