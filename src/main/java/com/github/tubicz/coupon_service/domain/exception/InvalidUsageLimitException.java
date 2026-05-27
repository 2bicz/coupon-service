package com.github.tubicz.coupon_service.domain.exception;

public class InvalidUsageLimitException extends RuntimeException {
    public InvalidUsageLimitException(String message) {
        super(message);
    }
}
