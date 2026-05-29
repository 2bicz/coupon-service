package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class CouponPersistenceAdapter implements CouponRepositoryPort {
    private final CouponRepository repository;

    @Override
    public String create(Coupon coupon) {
        return repository.save(toEntity(coupon)).getId().toString();
    }

    @Override
    public Optional<Coupon> getByCodeWithLock(String code) {
        return repository.findByCodeIgnoreCaseWithLock(code).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return repository.existsByCodeIgnoreCase(code);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(java.util.UUID.fromString(id));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(java.util.UUID.fromString(id));
    }

    private CouponEntity toEntity(Coupon domain) {
        var allowedCountries = domain.allowedCountryCodes().stream().map(CountryEntity::new).toList();
        return CouponEntity.builder()
                .code(domain.code())
                .usageLimit(domain.usageLimit())
                .countries(allowedCountries)
                .build();
    }

    private Coupon toDomain(CouponEntity entity) {
        return new Coupon(
                entity.getId().toString(),
                entity.getCode(),
                entity.getUsageLimit(),
                entity.getCountries().stream().map(CountryEntity::getCode).toList()
        );
    }
}
