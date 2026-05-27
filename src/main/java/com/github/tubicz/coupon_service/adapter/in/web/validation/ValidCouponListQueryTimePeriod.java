package com.github.tubicz.coupon_service.adapter.in.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CouponListQueryTimePeriodValidator.class)
public @interface ValidCouponListQueryTimePeriod {
    String message() default "Invalid time period. 'From' date can't be after 'to' date!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
