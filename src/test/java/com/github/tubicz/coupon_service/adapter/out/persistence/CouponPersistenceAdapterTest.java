package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ImportAutoConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
@Import({CouponPersistenceAdapter.class, CountryPersistenceAdapter.class})
@Testcontainers
class CouponPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    CouponPersistenceAdapter couponAdapter;

    @Autowired
    CountryPersistenceAdapter countryAdapter;

    @Test
    void createReturnsCouponUuidString() {
        String id = couponAdapter.create(new Coupon("NEWCOUPON", 10, List.of("US")));

        assertThatCode(() -> UUID.fromString(id)).doesNotThrowAnyException();
    }

    @Test
    void existsByCodeCaseInsensitiveReturnsFalseForNonExistent() {
        assertThat(couponAdapter.existsByCodeCaseInsensitive("MISSING")).isFalse();
    }

    @Test
    void existsByCodeCaseInsensitiveReturnsTrueAfterCreate() {
        couponAdapter.create(new Coupon("CASECHECK", 5, List.of("DE")));

        assertThat(couponAdapter.existsByCodeCaseInsensitive("CASECHECK")).isTrue();
        assertThat(couponAdapter.existsByCodeCaseInsensitive("casecheck")).isTrue();
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
        couponAdapter.create(new Coupon("PAGED1", 5, List.of("US")));
        couponAdapter.create(new Coupon("PAGED2", 5, List.of("DE")));

        CouponPage page = couponAdapter.getAll(new GetCouponsQuery(0, 10, null, null, null));

        assertThat(page.content()).extracting("code").contains("PAGED1", "PAGED2");
        assertThat(page.totalElements()).isGreaterThanOrEqualTo(2);
    }

    @Test
    void getAllWithSearchFilterReturnsMatchingCoupons() {
        couponAdapter.create(new Coupon("SEARCHME", 5, List.of("US")));
        couponAdapter.create(new Coupon("OTHER", 5, List.of("US")));

        CouponPage page = couponAdapter.getAll(new GetCouponsQuery(0, 10, "search", null, null));

        assertThat(page.content()).extracting("code").containsOnly("SEARCHME");
    }

    @Test
    void getAllWithCreatedAtFromFilterExcludesOlderCoupons() {
        couponAdapter.create(new Coupon("DATETEST1", 5, List.of("US")));
        Instant afterCreate = Instant.now();
        couponAdapter.create(new Coupon("DATETEST2", 5, List.of("US")));

        CouponPage page = couponAdapter.getAll(new GetCouponsQuery(0, 10, null, afterCreate, null));

        assertThat(page.content()).extracting("code").containsOnly("DATETEST2");
    }

    @Test
    void getAllRespectsPagination() {
        for (int i = 0; i < 5; i++) {
            couponAdapter.create(new Coupon("PGTEST" + i, 1, List.of("PL")));
        }

        CouponPage page = couponAdapter.getAll(new GetCouponsQuery(0, 2, "pgtest", null, null));

        assertThat(page.content()).hasSize(2);
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.totalElements()).isGreaterThanOrEqualTo(5);
    }
}
