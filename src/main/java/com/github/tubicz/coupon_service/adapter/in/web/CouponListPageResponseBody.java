package com.github.tubicz.coupon_service.adapter.in.web;

import java.net.URI;
import java.util.List;

record CouponListPageResponseBody(
        List<CouponViewResponseBody> content,
        int page,
        int size,
        long totalElements,
        URI first,
        URI previous,
        URI next,
        URI last
) {}
