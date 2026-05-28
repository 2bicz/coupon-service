package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface ExternalSystemRepository extends JpaRepository<ExternalSystemEntity, UUID> {
    Optional<ExternalSystemEntity> findByClientId(String clientId);
}
