package com.github.tubicz.coupon_service.application.port.in;

public interface CouponCreationUseCase {
    String create(CreateCouponCommand command);
}
