package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.CouponRedemptionRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class CouponRedemptionPersistenceAdapter implements CouponRedemptionRepositoryPort {
    private final CouponRedemptionJpaRepository jpa;

    @Override
    public boolean existsByCouponIdAndUserId(String couponId, String externalUserId) {
        return jpa.existsByCouponIdAndExternalUserId(UUID.fromString(couponId), UUID.fromString(externalUserId));
    }

    @Override
    public int countByCouponId(String couponId) {
        return jpa.countByCouponId(UUID.fromString(couponId));
    }

    @Override
    public void save(String couponId, String externalUserId) {
        jpa.save(CouponRedemptionEntity.builder()
                .couponId(UUID.fromString(couponId))
                .externalUserId(UUID.fromString(externalUserId))
                .build());
    }
}
