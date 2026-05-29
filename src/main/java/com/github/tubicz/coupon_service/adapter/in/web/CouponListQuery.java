package com.github.tubicz.coupon_service.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@ValidCouponListQueryTimePeriod
@Schema(description = "Query parameters for listing coupons")
record CouponListQuery(
        @Schema(description = "Page number (0-based)", example = "0")
        @Min(0) int page,

        @Schema(description = "Page size (1–100)", example = "20")
        @Min(1) @Max(100) int size,

        @Schema(description = "Filter by coupon code (partial match)")
        @Size(max = 255) String search,

        @Schema(description = "Return coupons created from this timestamp (inclusive)")
        Instant createdAtFrom,

        @Schema(description = "Return coupons created until this timestamp (inclusive)")
        Instant createdAtTo
) {}
