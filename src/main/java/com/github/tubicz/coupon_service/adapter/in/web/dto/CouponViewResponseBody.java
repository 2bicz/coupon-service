package com.github.tubicz.coupon_service.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;

public record CouponViewResponseBody(
        String code,
        Instant createdAt,
        int usageLimit,
        int usageCount,
        List<String> allowedCountryCodes
) {}
