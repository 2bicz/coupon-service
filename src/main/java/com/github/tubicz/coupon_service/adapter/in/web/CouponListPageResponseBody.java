package com.github.tubicz.coupon_service.adapter.in.web;

import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.util.List;

@Schema(description = "Paginated list of coupons")
record CouponListPageResponseBody(
        @Schema(description = "Coupons on the current page")
        List<CouponViewResponseBody> content,

        @Schema(description = "Current page number (0-based)", example = "0")
        int page,

        @Schema(description = "Page size", example = "20")
        int size,

        @Schema(description = "Total number of coupons matching the query", example = "150")
        long totalElements,

        @Schema(description = "URI of the first page")
        URI first,

        @Schema(description = "URI of the previous page; null if on the first page")
        URI previous,

        @Schema(description = "URI of the next page; null if on the last page")
        URI next,

        @Schema(description = "URI of the last page")
        URI last
) {}
