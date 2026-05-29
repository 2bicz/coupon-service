package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
@Import({CouponQueryAdapter.class, CouponPersistenceAdapter.class})
@Testcontainers
class CouponQueryAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    CouponPersistenceAdapter couponAdapter;

    @Autowired
    CouponQueryAdapter queryAdapter;

    @Test
    void getByIdReturnsCouponViewWithCorrectFields() {
        String id = couponAdapter.create(new Coupon(null, "QTESTVIEW", 5, List.of("US", "DE")));

        Optional<CouponView> result = queryAdapter.getById(id);

        assertThat(result).isPresent();
        CouponView view = result.get();
        assertThat(view.id()).isEqualTo(id);
        assertThat(view.code()).isEqualTo("QTESTVIEW");
        assertThat(view.usageLimit()).isEqualTo(5);
        assertThat(view.usageCount()).isZero();
        assertThat(view.allowedCountryCodes()).containsExactlyInAnyOrder("US", "DE");
        assertThat(view.createdAt()).isNotNull();
    }

    @Test
    void getByIdReturnsEmptyWhenNotFound() {
        Optional<CouponView> result = queryAdapter.getById(UUID.randomUUID().toString());

        assertThat(result).isEmpty();
    }

    @Test
    void getAllWithCreatedAtToFilterExcludesNewerCoupons() {
        couponAdapter.create(new Coupon(null, "EARLY", 5, List.of("US")));
        Instant cutoff = Instant.now();
        couponAdapter.create(new Coupon(null, "LATE", 5, List.of("US")));

        CouponPage page = queryAdapter.getAll(new GetCouponsQuery(0, 10, null, null, cutoff));

        assertThat(page.content()).extracting("code").containsOnly("EARLY");
    }
}
