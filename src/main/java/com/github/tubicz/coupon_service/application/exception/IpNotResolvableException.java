package com.github.tubicz.coupon_service.application.exception;

public class IpNotResolvableException extends RuntimeException {
    public IpNotResolvableException(String ip) {
        super("Country could not be determined for IP address: %s".formatted(ip));
    }
}
