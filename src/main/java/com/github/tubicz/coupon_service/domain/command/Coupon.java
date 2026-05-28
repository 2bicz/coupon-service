package com.github.tubicz.coupon_service.domain.command;

import com.github.tubicz.coupon_service.domain.exception.CouponExhaustedException;
import com.github.tubicz.coupon_service.domain.exception.CouponNotEligibleForCountryException;
import com.github.tubicz.coupon_service.domain.exception.InvalidCouponCodeFormatException;

import java.util.List;

public record Coupon(
        String id,
        String code,
        int usageLimit,
        List<String> allowedCountryCodes
) {
    public Coupon {
        code = validateAndNormalizeCouponCode(code);
        allowedCountryCodes = List.copyOf(allowedCountryCodes);
    }

    public void validateEligibility(int usageCount, String countryCode) {
        if (isUsedUp(usageCount)) {
            throw new CouponExhaustedException(code);
        }

        if (!allowsCountry(countryCode)) {
            throw new CouponNotEligibleForCountryException(code, countryCode);
        }
    }

    private boolean allowsCountry(String countryCode) {
        return allowedCountryCodes.contains(countryCode);
    }

    private boolean isUsedUp(int usageCount) {
        return usageCount >= usageLimit;
    }

    private String validateAndNormalizeCouponCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidCouponCodeFormatException(code);
        }

        return code.strip().toUpperCase();
    }
}
