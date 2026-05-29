package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.ExternalPartyRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
class ExternalPartyPersistenceAdapter implements ExternalPartyRepositoryPort {
    private final ExternalSystemRepository systemRepository;
    private final ExternalUserRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String findOrCreateExternalUserId(String systemClientId, String originUserId) {
        var system = findOrCreateSystem(systemClientId);
        return findOrCreateUser(system.getId(), originUserId).getId().toString();
    }

    private ExternalSystemEntity findOrCreateSystem(String clientId) {
        return systemRepository.findByClientId(clientId).orElseGet(() -> {
            try {
                return systemRepository.saveAndFlush(ExternalSystemEntity.builder()
                        .clientId(clientId)
                        .name(clientId)
                        .clientSecretHash("")
                        .build());
            } catch (DataIntegrityViolationException e) {
                return systemRepository.findByClientId(clientId).orElseThrow();
            }
        });
    }

    private ExternalUserEntity findOrCreateUser(UUID systemId, String originUserId) {
        return userRepository.findByExternalSystemIdAndOriginUserId(systemId, originUserId).orElseGet(() -> {
            try {
                return userRepository.saveAndFlush(ExternalUserEntity.builder()
                        .externalSystemId(systemId)
                        .originUserId(originUserId)
                        .build());
            } catch (DataIntegrityViolationException e) {
                return userRepository.findByExternalSystemIdAndOriginUserId(systemId, originUserId).orElseThrow();
            }
        });
    }
}
