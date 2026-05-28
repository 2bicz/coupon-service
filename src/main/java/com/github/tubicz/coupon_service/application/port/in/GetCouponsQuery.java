package com.github.tubicz.coupon_service.application.port.in;

import java.time.Instant;

public record GetCouponsQuery(
        int page,
        int size,
        String search,
        Instant createdAtFrom,
        Instant createdAtTo
) {}
