package com.github.tubicz.coupon_service.application.port.in;

public interface CouponRedemptionUseCase {
    void redeem(RedeemCouponCommand command);
}
