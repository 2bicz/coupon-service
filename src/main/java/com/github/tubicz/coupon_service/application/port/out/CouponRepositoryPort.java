package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;

import java.util.Optional;

public interface CouponRepositoryPort {
    Optional<CouponView> getById(String id);
    CouponPage getAll(GetCouponsQuery query);
    boolean existsByCodeCaseInsensitive(String code);
    String create(Coupon coupon);
}
