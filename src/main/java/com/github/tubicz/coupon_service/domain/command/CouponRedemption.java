package com.github.tubicz.coupon_service.domain.command;

import java.time.Instant;

public record CouponRedemption(
        String couponId,
        String externalUserId,
        Instant createdAt
) {
}
