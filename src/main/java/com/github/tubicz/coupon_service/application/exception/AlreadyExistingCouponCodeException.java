package com.github.tubicz.coupon_service.application.exception;

public class AlreadyExistingCouponCodeException extends RuntimeException {
    public AlreadyExistingCouponCodeException(String couponCode) {
        super("Coupon code '%s' already exists!".formatted(couponCode));
    }
}
