package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

interface CouponViewRepository extends ReadOnlyRepository<CouponViewEntity, UUID>, JpaSpecificationExecutor<CouponViewEntity> {
}
