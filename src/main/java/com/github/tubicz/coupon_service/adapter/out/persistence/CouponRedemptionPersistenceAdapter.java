package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.CouponRedemptionRepositoryPort;
import com.github.tubicz.coupon_service.domain.command.CouponRedemption;
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
    public boolean existsByCouponId(String couponId) {
        return jpa.existsByCouponId(UUID.fromString(couponId));
    }

    @Override
    public int countByCouponId(String couponId) {
        return jpa.countByCouponId(UUID.fromString(couponId));
    }

    @Override
    public void save(CouponRedemption couponRedemption) {
        jpa.save(CouponRedemptionEntity.builder()
                .couponId(UUID.fromString(couponRedemption.couponId()))
                .externalUserId(UUID.fromString(couponRedemption.externalUserId()))
                .createdAt(couponRedemption.createdAt())
                .build());
    }
}
