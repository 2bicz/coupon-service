package com.github.tubicz.coupon_service.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "Coupon details")
record CouponViewResponseBody(
        @Schema(description = "Coupon code", example = "SUMMER20")
        String code,

        @Schema(description = "Timestamp when the coupon was created")
        Instant createdAt,

        @Schema(description = "Maximum number of redemptions allowed", example = "100")
        int usageLimit,

        @Schema(description = "Number of times the coupon has been redeemed so far", example = "42")
        int usageCount,

        @Schema(description = "ISO 3166-1 alpha-2 country codes eligible to use this coupon", example = "[\"PL\", \"DE\"]")
        List<String> allowedCountryCodes
) {}
