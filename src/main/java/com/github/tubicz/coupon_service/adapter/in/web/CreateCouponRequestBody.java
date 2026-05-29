package com.github.tubicz.coupon_service.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@Schema(description = "Request body for creating a new coupon")
record CreateCouponRequestBody(
        @Schema(description = "Unique coupon code", example = "SUMMER20")
        @NotBlank String code,

        @Schema(description = "Maximum number of times the coupon can be redeemed", example = "100")
        int usageLimit,

        @Schema(description = "ISO 3166-1 alpha-2 country codes eligible to use this coupon", example = "[\"PL\", \"DE\"]")
        @NotEmpty List<@ValidCountryCode String> countryCodes
) {}
