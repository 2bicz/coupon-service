package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.command.CouponRedemption;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
@Import({CouponRedemptionPersistenceAdapter.class, CouponPersistenceAdapter.class, ExternalPartyPersistenceAdapter.class})
@Testcontainers
class CouponRedemptionPersistenceAdapterTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Autowired
    CouponRedemptionPersistenceAdapter redemptionAdapter;

    @Autowired
    CouponPersistenceAdapter couponAdapter;

    @Autowired
    ExternalPartyPersistenceAdapter externalPartyAdapter;

    @Test
    void existsByCouponIdAndUserIdReturnsFalseWhenNoRedemption() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST1", 10, List.of("US"), Instant.now()));
        String userId = externalPartyAdapter.findOrCreateExternalUserId("sys", "u1");

        assertThat(redemptionAdapter.existsByCouponIdAndUserId(couponId, userId)).isFalse();
    }

    @Test
    void existsByCouponIdAndUserIdReturnsTrueAfterSave() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST2", 10, List.of("US"), Instant.now()));
        String userId = externalPartyAdapter.findOrCreateExternalUserId("sys", "u2");

        redemptionAdapter.save(new CouponRedemption(couponId, userId, Instant.now()));

        assertThat(redemptionAdapter.existsByCouponIdAndUserId(couponId, userId)).isTrue();
    }

    @Test
    void countByCouponIdReturnsZeroWhenNoRedemptions() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST3", 10, List.of("US"), Instant.now()));

        assertThat(redemptionAdapter.countByCouponId(couponId)).isZero();
    }

    @Test
    void countByCouponIdReturnsCorrectCount() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST4", 10, List.of("US"), Instant.now()));
        String user1 = externalPartyAdapter.findOrCreateExternalUserId("sys", "uA");
        String user2 = externalPartyAdapter.findOrCreateExternalUserId("sys", "uB");

        redemptionAdapter.save(new CouponRedemption(couponId, user1, Instant.now()));
        redemptionAdapter.save(new CouponRedemption(couponId, user2, Instant.now()));

        assertThat(redemptionAdapter.countByCouponId(couponId)).isEqualTo(2);
    }

    @Test
    void countByCouponIdDoesNotCountOtherCoupons() {
        String couponId1 = couponAdapter.create(new Coupon(null, "RDTEST5A", 10, List.of("US"), Instant.now()));
        String couponId2 = couponAdapter.create(new Coupon(null, "RDTEST5B", 10, List.of("US"), Instant.now()));
        String userId = externalPartyAdapter.findOrCreateExternalUserId("sys", "uC");

        redemptionAdapter.save(new CouponRedemption(couponId1, userId, Instant.now()));

        assertThat(redemptionAdapter.countByCouponId(couponId1)).isEqualTo(1);
        assertThat(redemptionAdapter.countByCouponId(couponId2)).isZero();
    }
}
