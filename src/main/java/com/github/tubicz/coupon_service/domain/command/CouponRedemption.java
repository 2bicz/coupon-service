package com.github.tubicz.coupon_service.domain.command;

public record CouponRedemption(
        String couponCode,
        String externalUser,
        String externalSystem
) {
}
