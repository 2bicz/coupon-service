package com.github.tubicz.coupon_service.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RedeemCouponRequestBody(
        @NotBlank String user,
        @NotBlank String system
) {}
