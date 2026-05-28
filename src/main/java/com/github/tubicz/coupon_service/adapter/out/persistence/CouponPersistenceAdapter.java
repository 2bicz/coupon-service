package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class CouponPersistenceAdapter implements CouponRepositoryPort {
    private final CouponRepository write;
    private final CouponViewRepository read;

    @Override
    public Optional<CouponView> getById(String id) {
        return read.findById(UUID.fromString(id)).map(this::toDomain);
    }

    @Override
    public boolean existsByCodeCaseInsensitive(String code) {
        return read.existsByCodeIgnoreCase(code);
    }

    @Override
    public String create(Coupon coupon) {
        return write.save(fromDomain(coupon)).getId().toString();
    }

    private CouponEntity fromDomain(Coupon domain) {
        var allowedCountries = domain.allowedCountryCodes().stream().map(CountryEntity::new).toList();
        return CouponEntity.builder()
                .code(domain.code())
                .usageLimit(domain.usageLimit())
                .countries(allowedCountries)
                .build();
    }

    private CouponView toDomain(CouponViewEntity entity) {
        return new CouponView(
                entity.getId(),
                entity.getCode(),
                entity.getCreatedAt(),
                entity.getUsageLimit(),
                entity.getUsageCount(),
                entity.getCountryCodes()
        );
    }
}
