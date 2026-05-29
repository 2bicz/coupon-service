package com.github.tubicz.coupon_service.adapter.in.web;

import com.github.tubicz.coupon_service.application.port.in.CouponRedemptionUseCase;
import com.github.tubicz.coupon_service.application.port.in.RedeemCouponCommand;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon-redemption")
class CouponRedemptionController {
    private final CouponRedemptionUseCase couponRedemptionUseCase;

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
