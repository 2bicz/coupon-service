package com.github.tubicz.coupon_service.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request body for redeeming a coupon")
record CreateCouponRedemptionRequestBody(
        @Schema(description = "Coupon code to redeem", example = "SUMMER20")
        @NotBlank String couponCode,

        @Schema(description = "Unique identifier of the user in the external system", example = "user-123")
        @NotBlank String externalUser,

        @Schema(description = "Identifier of the external system (client ID)", example = "my-app")
        @NotBlank String externalSystem
) {}
