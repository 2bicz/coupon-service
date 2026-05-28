package com.github.tubicz.coupon_service.adapter.out.persistence;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

interface CouponRepository extends JpaRepository<CouponEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CouponEntity c WHERE UPPER(c.code) = UPPER(:code)")
    Optional<CouponEntity> findByCodeIgnoreCaseWithLock(@Param("code") String code);

    boolean existsByCodeIgnoreCase(String code);
}