package com.github.tubicz.coupon_service.adapter.out.persistence;

import java.util.UUID;

interface CouponViewRepository extends ReadOnlyRepository<CouponViewEntity, UUID> {
    boolean existsByCodeIgnoreCase(String code);
}
