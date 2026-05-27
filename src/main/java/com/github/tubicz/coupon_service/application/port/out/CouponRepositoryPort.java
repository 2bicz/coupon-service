package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.domain.command.Coupon;

public interface CouponRepositoryPort {
    boolean existsByCodeCaseInsensitive(String code);
    String create(Coupon coupon);
}
