package com.github.tubicz.coupon_service.domain.query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CouponView(
        UUID id,
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
