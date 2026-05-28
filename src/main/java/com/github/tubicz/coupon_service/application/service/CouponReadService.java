package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CouponReadUseCase;
import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class CouponReadService implements CouponReadUseCase {
    private final CouponRepositoryPort couponRepository;

    @Override
    public CouponView getCouponById(String id) {
        return couponRepository.getById(id).orElseThrow(
                () -> new CouponNotFoundException("Coupon with id %s could not be found".formatted(id))
        );
    }

    @Override
    public CouponPage getAll(GetCouponsQuery query) {
        return couponRepository.getAll(query);
    }
}
