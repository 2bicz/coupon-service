package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface CouponRepository extends JpaRepository<CouponEntity, UUID> {}