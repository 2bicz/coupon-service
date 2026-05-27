package com.github.tubicz.coupon_service.adapter.in.web.dto;

import java.time.Instant;
import java.util.List;

public record CouponDto(
        String code,
        Instant createdAt,
        int usageLimit,
        int usageCount,
        List<String> allowedCountryCodes
) {}
