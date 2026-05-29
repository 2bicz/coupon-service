package com.github.tubicz.coupon_service.adapter.in.web;

import com.github.tubicz.coupon_service.application.port.in.CouponRedemptionUseCase;
import com.github.tubicz.coupon_service.application.port.in.RedeemCouponCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Coupon Redemptions", description = "Coupon redemption management")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon-redemption")
class CouponRedemptionController {
    private final CouponRedemptionUseCase couponRedemptionUseCase;

    @Operation(
            summary = "Redeem a coupon",
            description = "Redeems a coupon for a given external user. The caller's IP address is resolved to a country code and validated against the coupon's country restrictions. When testing locally, see **docs/running.md** in the repository for setup instructions."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Coupon redeemed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Missing or invalid API key",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "403", description = "Coupon not eligible for the caller's country",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Coupon not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Coupon exhausted or already redeemed by this user",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Caller IP address could not be resolved to a country",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    ResponseEntity<Void> createCouponRedemption(@RequestBody @Valid CreateCouponRedemptionRequestBody requestBody, HttpServletRequest request) {
        couponRedemptionUseCase.redeem(new RedeemCouponCommand(
                requestBody.couponCode(),
                requestBody.externalUser(),
                requestBody.externalSystem(),
                IpExtractor.extract(request)
        ));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
