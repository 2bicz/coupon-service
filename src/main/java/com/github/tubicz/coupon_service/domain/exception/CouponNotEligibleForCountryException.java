package com.github.tubicz.coupon_service.domain.exception;

public class CouponNotEligibleForCountryException extends RuntimeException {
    public CouponNotEligibleForCountryException(String couponCode, String countryCode) {
        super("Coupon '%s' is not eligible for country: %s".formatted(couponCode, countryCode));
    }
}
