package com.github.tubicz.coupon_service.application.port.out;

public interface CouponRedemptionRepositoryPort {
    boolean existsByCouponIdAndUserId(String couponId, String externalUserId);
    int countByCouponId(String couponId);
    void save(String couponId, String externalUserId);
}
