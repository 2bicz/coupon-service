package com.github.tubicz.coupon_service.adapter.in.web;

import com.github.tubicz.coupon_service.adapter.in.web.dto.*;
import com.github.tubicz.coupon_service.application.port.in.CouponCreationUseCase;
import com.github.tubicz.coupon_service.application.port.in.CouponReadUseCase;
import com.github.tubicz.coupon_service.application.port.in.CreateCouponCommand;
import com.github.tubicz.coupon_service.domain.query.CouponView;
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
    private final CouponReadUseCase couponReadUseCase;

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

    @GetMapping("/{id}")
    ResponseEntity<CouponViewResponseBody> getCouponById(@PathVariable String id) {
        CouponView coupon = couponReadUseCase.getCouponById(id);
        var response = new CouponViewResponseBody(
                coupon.code(),
                coupon.createdAt(),
                coupon.usageLimit(),
                coupon.usageCount(),
                coupon.allowedCountryCodes()
        );
        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    ResponseEntity<CouponListPageResponseBody> getListOfCoupons(@Valid CouponListQuery queryParams) {
//
//    }
//
//
//    todo: Move it to separate controller '/coupon-redemption'
//    @PostMapping("/{code}/redemption")
//    ResponseEntity<EntryCreatedResponseBody> redeemCouponByCode(@PathVariable String code, @RequestBody @Valid RedeemCouponRequestBody requestBody) {
//
//    }

}
