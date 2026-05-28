package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

class CouponViewSpecification {

    static Specification<CouponViewEntity> hasCodeLike(String search) {
        return (root, query, cb) -> (search == null || search.isBlank()) ? null
                : cb.like(cb.lower(root.get("code")), "%" + search.toLowerCase() + "%");
    }

    static Specification<CouponViewEntity> createdAtFrom(Instant from) {
        return (root, query, cb) -> from == null ? null
                : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    static Specification<CouponViewEntity> createdAtTo(Instant to) {
        return (root, query, cb) -> to == null ? null
                : cb.lessThan(root.get("createdAt"), to);
    }
}
