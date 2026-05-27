package com.github.tubicz.coupon_service.domain.query;

import java.time.Instant;
import java.util.List;

public record CouponView(
        String code,
        Instant createdAt,
        int usageLimit,
        int usageCount,
        List<String> allowedCountryCodes
) {
    public CouponView {
        allowedCountryCodes = List.copyOf(allowedCountryCodes);
    }

    public boolean allowsCountry(String countryCode) {
        return allowedCountryCodes.contains(countryCode);
    }

    public boolean isUsedUp() {
        return usageCount >= usageLimit;
    }
}
