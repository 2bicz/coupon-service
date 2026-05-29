package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.CouponHasRedemptionsException;
import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CouponDeletionUseCase;
import com.github.tubicz.coupon_service.application.port.out.CouponRedemptionRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CouponDeletionService implements CouponDeletionUseCase {
    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponRedemptionRepositoryPort couponRedemptionRepositoryPort;

    @Override
    public void delete(String id) {
        if (!couponRepositoryPort.existsById(id)) {
            throw new CouponNotFoundException("Coupon with id '%s' not found".formatted(id));
        }
        if (couponRedemptionRepositoryPort.existsByCouponId(id)) {
            throw new CouponHasRedemptionsException(id);
        }
        couponRepositoryPort.deleteById(id);
    }
}
