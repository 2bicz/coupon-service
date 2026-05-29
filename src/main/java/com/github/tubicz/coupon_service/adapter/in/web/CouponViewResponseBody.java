package com.github.tubicz.coupon_service.adapter.in.web;

import java.time.Instant;
import java.util.List;

record CouponViewResponseBody(
        String code,
        Instant createdAt,
        int usageLimit,
        int usageCount,
        List<String> allowedCountryCodes
) {}
