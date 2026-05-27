package com.github.tubicz.coupon_service.adapter.in.web.validation;

import com.github.tubicz.coupon_service.adapter.in.web.dto.CouponListQuery;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CouponListQueryTimePeriodValidator implements ConstraintValidator<ValidCouponListQueryTimePeriod, CouponListQuery> {
    @Override
    public boolean isValid(CouponListQuery query, ConstraintValidatorContext context) {
        if (query.createdAtFrom() != null && query.createdAtTo() != null) {
            return query.createdAtFrom().isBefore(query.createdAtTo());
        }

        return true;
    }
}
