package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.domain.command.CouponRedemption;

public interface CouponRedemptionRepositoryPort {
    boolean existsByCouponIdAndUserId(String couponId, String externalUserId);
    boolean existsByCouponId(String couponId);
    int countByCouponId(String couponId);
    void save(CouponRedemption couponRedemption);
}
