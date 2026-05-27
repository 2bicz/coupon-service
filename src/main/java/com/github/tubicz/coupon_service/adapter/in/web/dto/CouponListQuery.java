package com.github.tubicz.coupon_service.adapter.in.web.dto;

import com.github.tubicz.coupon_service.adapter.in.web.validation.ValidCouponListQueryTimePeriod;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@ValidCouponListQueryTimePeriod
public record CouponListQuery(
        @Min(0) int page,
        @Min(1) @Max(100) int size,
        @Size(max = 255) String search,
        Instant createdAtFrom,
        Instant createdAtTo
) {}
