package com.github.tubicz.coupon_service.domain.exception;

public class InvalidCouponCodeFormatException extends RuntimeException {
    public InvalidCouponCodeFormatException(String couponCode) {
        super("Format of coupon code '%s' is invalid. It shouldn't be null nor blank!".formatted(couponCode));
    }
}
