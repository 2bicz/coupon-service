package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

class CouponViewSpecification {

    static Specification<CouponViewEntity> hasCodeLike(String search) {
        return (root, _, cb) -> (search == null || search.isBlank()) ? null
                : cb.like(cb.upper(root.get("code")), "%" + search.toUpperCase() + "%");
    }

    static Specification<CouponViewEntity> createdAtFrom(Instant from) {
        return (root, _, cb) -> from == null ? null
                : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    static Specification<CouponViewEntity> createdAtTo(Instant to) {
        return (root, _, cb) -> to == null ? null
                : cb.lessThan(root.get("createdAt"), to);
    }
}
