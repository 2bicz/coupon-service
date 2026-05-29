package com.github.tubicz.coupon_service.domain.command;

import com.github.tubicz.coupon_service.domain.exception.CouponExhaustedException;
import com.github.tubicz.coupon_service.domain.exception.CouponNotEligibleForCountryException;
import com.github.tubicz.coupon_service.domain.exception.InvalidCouponCodeFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @ParameterizedTest
    void nullOrBlankCodeThrows(String code) {
        assertThatThrownBy(() -> new Coupon(null, code, 10, List.of(), Instant.now()))
                .isInstanceOf(InvalidCouponCodeFormatException.class);
    }

    @Test
    void codeIsUppercased() {
        var coupon = new Coupon(null, "summer20", 10, List.of(), Instant.now());
        assertThat(coupon.code()).isEqualTo("SUMMER20");
    }

    @Test
    void codeIsStrippedAndUppercased() {
        var coupon = new Coupon(null, "  promo  ", 10, List.of(), Instant.now());
        assertThat(coupon.code()).isEqualTo("PROMO");
    }

    @Test
    void alreadyUppercaseCodeUnchanged() {
        var coupon = new Coupon(null, "SAVE10", 10, List.of(), Instant.now());
        assertThat(coupon.code()).isEqualTo("SAVE10");
    }

    @Test
    void allowedCountryCodesIsImmutable() {
        var mutable = new ArrayList<>(List.of("US", "DE"));
        var coupon = new Coupon(null, "CODE", 5, mutable, Instant.now());
        mutable.add("PL");
        assertThat(coupon.allowedCountryCodes()).containsExactly("US", "DE");
    }

    @Test
    void returnedAllowedCountryCodesIsUnmodifiable() {
        var coupon = new Coupon(null, "CODE", 5, List.of("US"), Instant.now());
        assertThatThrownBy(() -> coupon.allowedCountryCodes().add("DE"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void validateEligibilityDoesNotThrowWhenBelowUsageLimit() {
        var coupon = new Coupon(null, "CODE", 10, List.of("US"), Instant.now());
        assertThatCode(() -> coupon.validateEligibility(9, "US")).doesNotThrowAnyException();
    }

    @Test
    void validateEligibilityThrowsWhenUsageEqualsLimit() {
        var coupon = new Coupon(null, "CODE", 10, List.of("US"), Instant.now());
        assertThatThrownBy(() -> coupon.validateEligibility(10, "US"))
                .isInstanceOf(CouponExhaustedException.class);
    }

    @Test
    void validateEligibilityThrowsWhenUsageExceedsLimit() {
        var coupon = new Coupon(null, "CODE", 10, List.of("US"), Instant.now());
        assertThatThrownBy(() -> coupon.validateEligibility(11, "US"))
                .isInstanceOf(CouponExhaustedException.class);
    }

    @Test
    void validateEligibilityThrowsWhenZeroLimit() {
        var coupon = new Coupon(null, "CODE", 0, List.of("US"), Instant.now());
        assertThatThrownBy(() -> coupon.validateEligibility(0, "US"))
                .isInstanceOf(CouponExhaustedException.class);
    }

    @Test
    void validateEligibilityDoesNotThrowWhenCountryAllowed() {
        var coupon = new Coupon(null, "CODE", 10, List.of("US", "DE"), Instant.now());
        assertThatCode(() -> coupon.validateEligibility(0, "DE")).doesNotThrowAnyException();
    }

    @Test
    void validateEligibilityThrowsWhenCountryNotAllowed() {
        var coupon = new Coupon(null, "CODE", 10, List.of("US"), Instant.now());
        assertThatThrownBy(() -> coupon.validateEligibility(0, "PL"))
                .isInstanceOf(CouponNotEligibleForCountryException.class);
    }

    @Test
    void validateEligibilityThrowsWhenCountryListEmpty() {
        var coupon = new Coupon(null, "CODE", 10, List.of(), Instant.now());
        assertThatThrownBy(() -> coupon.validateEligibility(0, "US"))
                .isInstanceOf(CouponNotEligibleForCountryException.class);
    }
}
