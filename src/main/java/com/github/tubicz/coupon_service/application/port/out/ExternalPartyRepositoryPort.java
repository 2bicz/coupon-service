package com.github.tubicz.coupon_service.application.port.out;

public interface ExternalPartyRepositoryPort {
    String findOrCreateExternalUserId(String systemClientId, String originUserId);
}
