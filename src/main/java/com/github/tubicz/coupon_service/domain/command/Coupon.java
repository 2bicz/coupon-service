package com.github.tubicz.coupon_service.domain.command;

import com.github.tubicz.coupon_service.domain.exception.InvalidCouponCodeFormatException;

import java.util.List;

public record Coupon(
        String code,
        int usageLimit,
        List<String> allowedCountryCodes
) {
    public Coupon {
        code = validateAndNormalizeCouponCode(code);
        allowedCountryCodes = List.copyOf(allowedCountryCodes);
    }

    private String validateAndNormalizeCouponCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidCouponCodeFormatException(code);
        }

        return code.strip().toUpperCase();
    }
}
