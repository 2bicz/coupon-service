package com.github.tubicz.coupon_service.adapter.in.web;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CountryCodeValidator.class)
@Target({
        ElementType.FIELD,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@interface ValidCountryCode {

    String message()
            default "Invalid country code format. Only ISO 3166-1 alpha-2 is allowed.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}