package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.out.CountryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
class CountryPersistenceAdapter implements CountryRepositoryPort {
    private final CountryRepository jpa;

    @Override
    public Set<String> findUnknownCountryCodes(Collection<String> countryCodes) {
        Set<String> existing = jpa.findExistingCodesAmong(countryCodes);
        Set<String> unknown = new HashSet<>(countryCodes);
        unknown.removeAll(existing);
        return unknown;
    }
}
