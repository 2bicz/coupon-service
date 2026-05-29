package com.github.tubicz.coupon_service.adapter.in.web;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class CouponListQueryTimePeriodValidator implements ConstraintValidator<ValidCouponListQueryTimePeriod, CouponListQuery> {
    @Override
    public boolean isValid(CouponListQuery query, ConstraintValidatorContext context) {
        if (query.createdAtFrom() != null && query.createdAtTo() != null) {
            return query.createdAtFrom().isBefore(query.createdAtTo());
        }

        return true;
    }
}
