package com.github.tubicz.coupon_service.application.port.out;

import java.util.Collection;
import java.util.Set;

public interface CountryRepositoryPort {
    Set<String> findUnknownCountryCodes(Collection<String> countryCodes);
}
