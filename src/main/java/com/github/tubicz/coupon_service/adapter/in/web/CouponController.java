package com.github.tubicz.coupon_service.adapter.in.web;

import com.github.tubicz.coupon_service.adapter.in.web.dto.*;
import com.github.tubicz.coupon_service.application.port.in.CouponCreationUseCase;
import com.github.tubicz.coupon_service.application.port.in.CreateCouponCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon")
class CouponController {
    private final CouponCreationUseCase couponCreationUseCase;

    @PostMapping
    ResponseEntity<Void> createCoupon(@RequestBody @Valid CreateCouponRequestBody requestBody) {
        var command = new CreateCouponCommand(
                requestBody.code(),
                requestBody.usageLimit(),
                requestBody.countryCodes()
        );
        String couponId = couponCreationUseCase.create(command);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(couponId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

//    @GetMapping
//    ResponseEntity<CouponListPageResponseBody> getListOfCoupons(@Valid CouponListQuery queryParams) {
//
//    }
//
//    @GetMapping("/{code}")
//    ResponseEntity<CouponResponseBody> getCouponByCode(@PathVariable String code) {
//
//    }
//
//    @PostMapping("/{code}/redemption")
//    ResponseEntity<EntryCreatedResponseBody> redeemCouponByCode(@PathVariable String code, @RequestBody @Valid RedeemCouponRequestBody requestBody) {
//
//    }

}
