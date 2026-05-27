package com.github.tubicz.coupon_service.application.port.in;

import java.util.List;
import java.util.Objects;

public record CreateCouponCommand(
        String code,
        int usageLimit,
        List<String> allowedCountryCodes
) {
    public CreateCouponCommand {
        allowedCountryCodes = List.copyOf(allowedCountryCodes);
    }
}
