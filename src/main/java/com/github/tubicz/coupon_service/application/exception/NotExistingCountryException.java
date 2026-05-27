package com.github.tubicz.coupon_service.application.exception;

import java.util.Collection;

public class NotExistingCountryException extends RuntimeException {
    public NotExistingCountryException(Collection<String> unknownCountries) {
        super("Countries with following codes couldn't be found: %s".formatted(String.join(", ", unknownCountries)));
    }
}
