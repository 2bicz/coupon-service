package com.github.tubicz.coupon_service.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupon_redemption", uniqueConstraints = @UniqueConstraint(columnNames = {"coupon_id", "external_user_id"}))
class CouponRedemptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    @Column(name = "external_user_id", nullable = false)
    private UUID externalUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
