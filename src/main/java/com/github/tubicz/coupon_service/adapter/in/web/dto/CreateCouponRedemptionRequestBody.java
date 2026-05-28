package com.github.tubicz.coupon_service.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCouponRedemptionRequestBody(
        @NotBlank String couponCode,
        @NotBlank String externalUser,
        @NotBlank String externalSystem
) {}
