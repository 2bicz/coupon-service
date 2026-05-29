package com.github.tubicz.coupon_service.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "external_user")
class ExternalUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_system_id", nullable = false)
    private UUID externalSystemId;

    @Column(name = "origin_user_id", nullable = false)
    private String originUserId;
}
