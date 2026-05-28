package com.github.tubicz.coupon_service.domain.query;

import java.util.List;

public record CouponPage(
        List<CouponView> content,
        int page,
        int size,
        long totalElements
) {}
