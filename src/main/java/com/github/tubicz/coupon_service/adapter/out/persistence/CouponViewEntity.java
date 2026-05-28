package com.github.tubicz.coupon_service.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Immutable
@Entity
@Table(name = "coupon_view")
class CouponViewEntity {
    @Id
    private UUID id;

    @Column(name = "code")
    private String code;

    @Column(name = "usage_limit")
    private int usageLimit;

    @Column(name = "usage_count")
    private int usageCount;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "country_codes")
    private List<String> countryCodes;
}
