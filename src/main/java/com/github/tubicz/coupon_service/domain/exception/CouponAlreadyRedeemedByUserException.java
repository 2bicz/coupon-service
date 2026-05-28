package com.github.tubicz.coupon_service.domain.exception;

public class CouponAlreadyRedeemedByUserException extends RuntimeException {
    public CouponAlreadyRedeemedByUserException(String couponCode, String userId) {
        super("Coupon '%s' has already been redeemed by user '%s'".formatted(couponCode, userId));
    }
}
