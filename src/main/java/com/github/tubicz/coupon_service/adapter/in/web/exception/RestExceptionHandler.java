package com.github.tubicz.coupon_service.adapter.in.web.exception;

import com.github.tubicz.coupon_service.application.exception.AlreadyExistingCouponCodeException;
import com.github.tubicz.coupon_service.domain.exception.CouponAlreadyRedeemedByUserException;
import com.github.tubicz.coupon_service.domain.exception.CouponExhaustedException;
import com.github.tubicz.coupon_service.domain.exception.CouponNotEligibleForCountryException;
import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.exception.CountryNotFoundException;
import com.github.tubicz.coupon_service.domain.exception.InvalidCouponCodeFormatException;
import com.github.tubicz.coupon_service.domain.exception.InvalidUsageLimitException;
import com.github.tubicz.coupon_service.application.exception.IpNotResolvableException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handle(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        message -> message.getDefaultMessage() == null ? "" : message.getDefaultMessage()
                ));

        return new ProblemDetailBuilder(HttpStatus.BAD_REQUEST)
                .title("Validation failed")
                .detail("Invalid request body")
                .errorCode(ErrorCode.INCORRECT_REQUEST_BODY)
                .errors(errors)
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handle(ConstraintViolationException exception) {
        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        return new ProblemDetailBuilder(HttpStatus.BAD_REQUEST)
                .title("Constraint violation")
                .detail("Invalid request param or path variable")
                .errorCode(ErrorCode.CONSTRAINT_VIOLATION)
                .errors(errors)
                .build();
    }

    @ExceptionHandler(InvalidUsageLimitException.class)
    public ProblemDetail handle(InvalidUsageLimitException exception) {
        return new ProblemDetailBuilder(HttpStatus.BAD_REQUEST)
                .title("Invalid coupon usage limit")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.INVALID_COUPON_USAGE_LIMIT)
                .build();
    }

    @ExceptionHandler(InvalidCouponCodeFormatException.class)
    public ProblemDetail handle(InvalidCouponCodeFormatException exception) {
        return new ProblemDetailBuilder(HttpStatus.BAD_REQUEST)
                .title("Invalid coupon code format")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.INVALID_COUPON_CODE_FORMAT)
                .build();
    }

    @ExceptionHandler(CouponNotFoundException.class)
    public ProblemDetail handle(CouponNotFoundException exception) {
        return new ProblemDetailBuilder(HttpStatus.NOT_FOUND)
                .title("Coupon not found")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUPON_NOT_FOUND)
                .build();
    }

    @ExceptionHandler(AlreadyExistingCouponCodeException.class)
    public ProblemDetail handle(AlreadyExistingCouponCodeException exception) {
        return new ProblemDetailBuilder(HttpStatus.CONFLICT)
                .title("Coupon already exists")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUPON_ALREADY_EXISTS)
                .build();
    }

    @ExceptionHandler(CountryNotFoundException.class)
    public ProblemDetail handle(CountryNotFoundException exception) {
        return new ProblemDetailBuilder(HttpStatus.NOT_FOUND)
                .title("Country does not exist")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUNTRY_NOT_FOUND)
                .build();
    }

    @ExceptionHandler(IpNotResolvableException.class)
    public ProblemDetail handle(IpNotResolvableException exception) {
        return new ProblemDetailBuilder(HttpStatus.UNPROCESSABLE_ENTITY)
                .title("IP address not resolvable")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.IP_NOT_RESOLVABLE)
                .build();
    }

    @ExceptionHandler(CouponNotEligibleForCountryException.class)
    public ProblemDetail handle(CouponNotEligibleForCountryException exception) {
        return new ProblemDetailBuilder(HttpStatus.FORBIDDEN)
                .title("Coupon not eligible for your country")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUPON_NOT_ELIGIBLE_FOR_COUNTRY)
                .build();
    }

    @ExceptionHandler(CouponExhaustedException.class)
    public ProblemDetail handle(CouponExhaustedException exception) {
        return new ProblemDetailBuilder(HttpStatus.CONFLICT)
                .title("Coupon exhausted")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUPON_EXHAUSTED)
                .build();
    }

    @ExceptionHandler(CouponAlreadyRedeemedByUserException.class)
    public ProblemDetail handle(CouponAlreadyRedeemedByUserException exception) {
        return new ProblemDetailBuilder(HttpStatus.CONFLICT)
                .title("Coupon already redeemed")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUPON_ALREADY_REDEEMED)
                .build();
    }
}
