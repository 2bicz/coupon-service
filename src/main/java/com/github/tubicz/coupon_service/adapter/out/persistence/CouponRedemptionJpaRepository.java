package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface CouponRedemptionJpaRepository extends JpaRepository<CouponRedemptionEntity, UUID> {
    boolean existsByCouponIdAndExternalUserId(UUID couponId, UUID externalUserId);
    boolean existsByCouponId(UUID couponId);
    int countByCouponId(UUID couponId);
}
