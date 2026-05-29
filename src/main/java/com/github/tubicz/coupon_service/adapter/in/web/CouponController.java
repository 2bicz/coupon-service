package com.github.tubicz.coupon_service.adapter.in.web;

import com.github.tubicz.coupon_service.adapter.in.web.dto.*;
import com.github.tubicz.coupon_service.application.port.in.CouponCreationUseCase;
import com.github.tubicz.coupon_service.application.port.in.CouponDeletionUseCase;
import com.github.tubicz.coupon_service.application.port.in.CouponReadUseCase;
import com.github.tubicz.coupon_service.application.port.in.CreateCouponCommand;
import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/coupon")
class CouponController {
    private final CouponCreationUseCase couponCreationUseCase;
    private final CouponReadUseCase couponReadUseCase;
    private final CouponDeletionUseCase couponDeletionUseCase;

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

    @GetMapping
    ResponseEntity<CouponListPageResponseBody> getListOfCoupons(@Valid CouponListQuery queryParams) {
        var query = new GetCouponsQuery(
                queryParams.page(),
                queryParams.size(),
                queryParams.search(),
                queryParams.createdAtFrom(),
                queryParams.createdAtTo()
        );

        CouponPage page = couponReadUseCase.getAll(query);

        List<CouponViewResponseBody> content = page.content().stream()
                .map(c -> new CouponViewResponseBody(
                        c.code(), c.createdAt(), c.usageLimit(), c.usageCount(), c.allowedCountryCodes()))
                .toList();

        int totalPages = (int) Math.ceil((double) page.totalElements() / page.size());

        URI first = buildPageUri(0, page.size());
        URI last = buildPageUri(Math.max(0, totalPages - 1), page.size());
        URI previous = page.page() > 0 ? buildPageUri(page.page() - 1, page.size()) : null;
        URI next = page.page() < totalPages - 1 ? buildPageUri(page.page() + 1, page.size()) : null;

        return ResponseEntity.ok(new CouponListPageResponseBody(
                content,
                page.page(),
                page.size(),
                page.totalElements(),
                first,
                previous,
                next,
                last
        ));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCoupon(@PathVariable String id) {
        couponDeletionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    private URI buildPageUri(int page, int size) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .build()
                .toUri();
    }

}
