package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.domain.command.Coupon;
import java.util.Optional;

public interface CouponRepositoryPort {
    Optional<Coupon> getByCodeWithLock(String code);
    boolean existsByCode(String code);
    String create(Coupon coupon);
}
