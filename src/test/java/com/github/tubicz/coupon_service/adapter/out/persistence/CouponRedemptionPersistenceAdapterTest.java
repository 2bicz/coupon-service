package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.domain.command.Coupon;
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
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST1", 10, List.of("US")));
        String userId = externalPartyAdapter.findOrCreateExternalUserId("sys", "u1");

        assertThat(redemptionAdapter.existsByCouponIdAndUserId(couponId, userId)).isFalse();
    }

    @Test
    void existsByCouponIdAndUserIdReturnsTrueAfterSave() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST2", 10, List.of("US")));
        String userId = externalPartyAdapter.findOrCreateExternalUserId("sys", "u2");

        redemptionAdapter.save(couponId, userId);

        assertThat(redemptionAdapter.existsByCouponIdAndUserId(couponId, userId)).isTrue();
    }

    @Test
    void countByCouponIdReturnsZeroWhenNoRedemptions() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST3", 10, List.of("US")));

        assertThat(redemptionAdapter.countByCouponId(couponId)).isZero();
    }

    @Test
    void countByCouponIdReturnsCorrectCount() {
        String couponId = couponAdapter.create(new Coupon(null, "RDTEST4", 10, List.of("US")));
        String user1 = externalPartyAdapter.findOrCreateExternalUserId("sys", "uA");
        String user2 = externalPartyAdapter.findOrCreateExternalUserId("sys", "uB");

        redemptionAdapter.save(couponId, user1);
        redemptionAdapter.save(couponId, user2);

        assertThat(redemptionAdapter.countByCouponId(couponId)).isEqualTo(2);
    }

    @Test
    void countByCouponIdDoesNotCountOtherCoupons() {
        String couponId1 = couponAdapter.create(new Coupon(null, "RDTEST5A", 10, List.of("US")));
        String couponId2 = couponAdapter.create(new Coupon(null, "RDTEST5B", 10, List.of("US")));
        String userId = externalPartyAdapter.findOrCreateExternalUserId("sys", "uC");

        redemptionAdapter.save(couponId1, userId);

        assertThat(redemptionAdapter.countByCouponId(couponId1)).isEqualTo(1);
        assertThat(redemptionAdapter.countByCouponId(couponId2)).isZero();
    }
}
