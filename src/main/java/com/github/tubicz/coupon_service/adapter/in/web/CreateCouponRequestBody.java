package com.github.tubicz.coupon_service.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

record CreateCouponRequestBody(
        @NotBlank String code,
        int usageLimit,
        @NotEmpty List<@ValidCountryCode String> countryCodes
) {}
