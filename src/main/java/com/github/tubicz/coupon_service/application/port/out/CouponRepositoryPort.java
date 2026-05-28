package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.query.CouponView;

import java.util.Optional;

public interface CouponRepositoryPort {
    Optional<CouponView> getById(String id);
    boolean existsByCodeCaseInsensitive(String code);
    String create(Coupon coupon);
}
