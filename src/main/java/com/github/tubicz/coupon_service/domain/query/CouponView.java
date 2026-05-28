package com.github.tubicz.coupon_service.domain.query;

import java.time.Instant;
import java.util.List;

public record CouponView(
        String id,
        String code,
        Instant createdAt,
        int usageLimit,
        int usageCount,
        List<String> allowedCountryCodes
) {
    public CouponView {
        allowedCountryCodes = List.copyOf(allowedCountryCodes);
    }
}
