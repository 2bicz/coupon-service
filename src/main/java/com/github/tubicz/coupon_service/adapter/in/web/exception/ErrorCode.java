package com.github.tubicz.coupon_service.adapter.in.web.exception;

enum ErrorCode {
    INCORRECT_REQUEST_BODY,
    CONSTRAINT_VIOLATION,
    INVALID_COUPON_CODE_FORMAT,
    INVALID_COUPON_USAGE_LIMIT,
    COUPON_ALREADY_EXISTS,
    COUNTRY_NOT_FOUND,
}
