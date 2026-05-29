package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
@Import({CouponPersistenceAdapter.class, CountryPersistenceAdapter.class, CouponQueryAdapter.class})
@Testcontainers
class CouponPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    CouponPersistenceAdapter couponAdapter;

    @Autowired
    CouponQueryAdapter couponQueryAdapter;

    @Autowired
    CountryPersistenceAdapter countryAdapter;

    @Test
    void createReturnsCouponUuidString() {
        String id = couponAdapter.create(new Coupon(null, "NEWCOUPON", 10, List.of("US"), Instant.now()));

        assertThat(UUID.fromString(id)).isNotNull();
    }

    @Test
    void existsByCodeCaseInsensitiveReturnsFalseForNonExistent() {
        assertThat(couponAdapter.existsByCode("MISSING")).isFalse();
    }

    @Test
    void existsByCodeCaseInsensitiveReturnsTrueAfterCreate() {
        couponAdapter.create(new Coupon(null, "CASECHECK", 5, List.of("DE"), Instant.now()));

        assertThat(couponAdapter.existsByCode("CASECHECK")).isTrue();
        assertThat(couponAdapter.existsByCode("casecheck")).isTrue();
    }

    @Test
    void findUnknownCountryCodesReturnsEmptyWhenAllExist() {
        assertThat(countryAdapter.findUnknownCountryCodes(List.of("US", "DE", "PL"))).isEmpty();
    }

    @Test
    void findUnknownCountryCodesReturnsUnknownCodes() {
        var unknown = countryAdapter.findUnknownCountryCodes(List.of("US", "XX", "ZZ"));

        assertThat(unknown).containsExactlyInAnyOrder("XX", "ZZ");
    }

    @Test
    void findUnknownCountryCodesReturnsAllWhenNoneExist() {
        assertThat(countryAdapter.findUnknownCountryCodes(List.of("XX", "YY")))
                .containsExactlyInAnyOrder("XX", "YY");
    }

    @Test
    void getAllWithNoFilterReturnsCoupons() {
        couponAdapter.create(new Coupon(null, "PAGED1", 5, List.of("US"), Instant.now()));
        couponAdapter.create(new Coupon(null, "PAGED2", 5, List.of("DE"), Instant.now()));

        CouponPage page = couponQueryAdapter.getAll(new GetCouponsQuery(0, 10, null, null, null));

        assertThat(page.content()).extracting("code").contains("PAGED1", "PAGED2");
        assertThat(page.totalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getAllWithSearchFilterReturnsMatchingCoupons() {
        couponAdapter.create(new Coupon(null, "SEARCHME", 5, List.of("US"), Instant.now()));
        couponAdapter.create(new Coupon(null, "OTHER", 5, List.of("US"), Instant.now()));

        CouponPage page = couponQueryAdapter.getAll(new GetCouponsQuery(0, 10, "search", null, null));

        assertThat(page.content()).extracting("code").containsOnly("SEARCHME");
    }

    @Test
    void getAllWithCreatedAtFromFilterExcludesOlderCoupons() {
        couponAdapter.create(new Coupon(null, "DATETEST1", 5, List.of("US"), Instant.now()));
        Instant afterCreate = Instant.now();
        couponAdapter.create(new Coupon(null, "DATETEST2", 5, List.of("US"), Instant.now()));

        CouponPage page = couponQueryAdapter.getAll(new GetCouponsQuery(0, 10, null, afterCreate, null));

        assertThat(page.content()).extracting("code").containsOnly("DATETEST2");
    }

    @Test
    void getAllRespectsPagination() {
        for (int i = 0; i < 5; i++) {
            couponAdapter.create(new Coupon(null, "PGTEST" + i, 1, List.of("PL"), Instant.now()));
        }

        CouponPage page = couponQueryAdapter.getAll(new GetCouponsQuery(0, 2, "pgtest", null, null));

        assertThat(page.content()).hasSize(2);
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.totalElements()).isGreaterThanOrEqualTo(5);
    }
}
