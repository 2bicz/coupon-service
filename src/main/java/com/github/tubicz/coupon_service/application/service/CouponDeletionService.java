package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CouponDeletionUseCase;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CouponDeletionService implements CouponDeletionUseCase {
    private final CouponRepositoryPort couponRepositoryPort;

    @Override
    public void delete(String id) {
        if (!couponRepositoryPort.existsById(id)) {
            throw new CouponNotFoundException("Coupon with id '%s' not found".formatted(id));
        }
        couponRepositoryPort.deleteById(id);
    }
}
