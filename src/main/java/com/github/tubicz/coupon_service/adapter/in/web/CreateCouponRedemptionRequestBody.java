package com.github.tubicz.coupon_service.adapter.in.web;

import jakarta.validation.constraints.NotBlank;

record CreateCouponRedemptionRequestBody(
        @NotBlank String couponCode,
        @NotBlank String externalUser,
        @NotBlank String externalSystem
) {}
