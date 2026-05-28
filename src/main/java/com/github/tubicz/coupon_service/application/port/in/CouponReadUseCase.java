package com.github.tubicz.coupon_service.application.port.in;

import com.github.tubicz.coupon_service.domain.query.CouponView;

public interface CouponReadUseCase {
    CouponView getCouponById(String id);
}
