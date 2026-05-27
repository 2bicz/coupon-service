package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class CouponPersistenceAdapter implements CouponRepositoryPort {
    private final CouponJpaRepository jpa;

    @Override
    public boolean existsByCodeCaseInsensitive(String code) {
        return jpa.existsByCodeIgnoreCase(code);
    }

    @Override
    public String create(Coupon coupon) {
        var allowedCountries = coupon.allowedCountryCodes().stream().map(CountryJpaEntity::new).toList();
        var jpaCoupon = CouponJpaEntity.builder()
                .code(coupon.code())
                .usageLimit(coupon.usageLimit())
                .countries(allowedCountries)
                .build();

        return jpa.save(jpaCoupon).getId().toString();
    }
}
