package com.github.tubicz.coupon_service.adapter.in.web.exception;

import com.github.tubicz.coupon_service.application.exception.AlreadyExistingCouponCodeException;
import com.github.tubicz.coupon_service.application.exception.NotExistingCountryException;
import com.github.tubicz.coupon_service.domain.exception.InvalidCouponCodeFormatException;
import com.github.tubicz.coupon_service.domain.exception.InvalidUsageLimitException;
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

    @ExceptionHandler(AlreadyExistingCouponCodeException.class)
    public ProblemDetail handle(AlreadyExistingCouponCodeException exception) {
        return new ProblemDetailBuilder(HttpStatus.CONFLICT)
                .title("Coupon already exists")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUPON_ALREADY_EXISTS)
                .build();
    }

    @ExceptionHandler(NotExistingCountryException.class)
    public ProblemDetail handle(NotExistingCountryException exception) {
        return new ProblemDetailBuilder(HttpStatus.NOT_FOUND)
                .title("Country does not exist")
                .detail(exception.getMessage())
                .errorCode(ErrorCode.COUNTRY_NOT_FOUND)
                .build();
    }
}
