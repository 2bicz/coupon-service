package com.github.tubicz.coupon_service.application.port.out;

import com.github.tubicz.coupon_service.application.exception.IpNotResolvableException;

public interface IpGeolocationPort {
    /**
     * @throws IpNotResolvableException if country cannot be determined for the given IP
     */
    String getCountryCode(String ipAddress);
}
