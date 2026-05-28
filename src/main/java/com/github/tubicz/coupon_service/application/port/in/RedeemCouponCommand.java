package com.github.tubicz.coupon_service.application.port.in;

public record RedeemCouponCommand(
        String couponCode,
        String externalUser,
        String externalSystem,
        String ipAddress
) {}
