package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;

import java.util.Optional;

public interface CouponViewRepositoryPort {
    Optional<CouponView> getById(String id);
    CouponPage getAll(GetCouponsQuery query);
}
