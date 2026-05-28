package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

interface ExternalUserRepository extends JpaRepository<ExternalUserEntity, UUID> {
    Optional<ExternalUserEntity> findByExternalSystemIdAndOriginUserId(UUID externalSystemId, String originUserId);
}
