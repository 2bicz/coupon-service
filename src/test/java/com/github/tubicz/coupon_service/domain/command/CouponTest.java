package com.github.tubicz.coupon_service.domain.command;

import com.github.tubicz.coupon_service.domain.exception.InvalidCouponCodeFormatException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @ParameterizedTest
    void nullOrBlankCodeThrows(String code) {
        assertThatThrownBy(() -> new Coupon(code, 10, List.of()))
                .isInstanceOf(InvalidCouponCodeFormatException.class);
    }

    @Test
    void codeIsUppercased() {
        var coupon = new Coupon("summer20", 10, List.of());
        assertThat(coupon.code()).isEqualTo("SUMMER20");
    }

    @Test
    void codeIsStrippedAndUppercased() {
        var coupon = new Coupon("  promo  ", 10, List.of());
        assertThat(coupon.code()).isEqualTo("PROMO");
    }

    @Test
    void alreadyUppercaseCodeUnchanged() {
        var coupon = new Coupon("SAVE10", 10, List.of());
        assertThat(coupon.code()).isEqualTo("SAVE10");
    }

    @Test
    void allowedCountryCodesIsImmutable() {
        var mutable = new ArrayList<>(List.of("US", "DE"));
        var coupon = new Coupon("CODE", 5, mutable);
        mutable.add("PL");
        assertThat(coupon.allowedCountryCodes()).containsExactly("US", "DE");
    }

    @Test
    void returnedAllowedCountryCodesIsUnmodifiable() {
        var coupon = new Coupon("CODE", 5, List.of("US"));
        assertThatThrownBy(() -> coupon.allowedCountryCodes().add("DE"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
