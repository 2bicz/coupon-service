package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

interface CouponJpaRepository extends JpaRepository<CouponJpaEntity, UUID> {
    boolean existsByCodeIgnoreCase(String code);
}