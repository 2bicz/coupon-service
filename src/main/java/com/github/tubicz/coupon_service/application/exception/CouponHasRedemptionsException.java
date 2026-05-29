package com.github.tubicz.coupon_service.application.exception;

public class CouponHasRedemptionsException extends RuntimeException {
    public CouponHasRedemptionsException(String couponId) {
        super("Coupon '%s' has already been redeemed and cannot be deleted".formatted(couponId));
    }
}
