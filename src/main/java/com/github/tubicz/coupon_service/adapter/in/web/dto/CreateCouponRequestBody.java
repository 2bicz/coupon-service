package com.github.tubicz.coupon_service.adapter.in.web.dto;

import com.github.tubicz.coupon_service.adapter.in.web.validation.ValidCountryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateCouponRequestBody(
        @NotBlank String code,
        int usageLimit,
        @NotEmpty List<@ValidCountryCode String> countryCodes
) {}
