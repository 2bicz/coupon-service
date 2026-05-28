package com.github.tubicz.coupon_service.domain.exception;

public class CouponExhaustedException extends RuntimeException {
    public CouponExhaustedException(String couponCode) {
        super("Coupon '%s' has reached its usage limit".formatted(couponCode));
    }
}
