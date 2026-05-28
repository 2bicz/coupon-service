package com.github.tubicz.coupon_service.domain.query;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponViewTest {

    private CouponView couponView(int usageLimit, int usageCount, List<String> countries) {
        return new CouponView(UUID.randomUUID(), "CODE", Instant.now(), usageLimit, usageCount, countries);
    }

    @Test
    void allowsCountryReturnsTrueWhenPresent() {
        assertThat(couponView(10, 0, List.of("US", "DE")).allowsCountry("DE")).isTrue();
    }

    @Test
    void allowsCountryReturnsFalseWhenAbsent() {
        assertThat(couponView(10, 0, List.of("US")).allowsCountry("PL")).isFalse();
    }

    @Test
    void allowsCountryReturnsFalseForEmptyList() {
        assertThat(couponView(10, 0, List.of()).allowsCountry("US")).isFalse();
    }

    @Test
    void isUsedUpReturnsFalseWhenBelowLimit() {
        assertThat(couponView(10, 9, List.of()).isUsedUp()).isFalse();
    }

    @Test
    void isUsedUpReturnsTrueWhenEqualToLimit() {
        assertThat(couponView(10, 10, List.of()).isUsedUp()).isTrue();
    }

    @Test
    void isUsedUpReturnsTrueWhenExceedsLimit() {
        assertThat(couponView(10, 11, List.of()).isUsedUp()).isTrue();
    }

    @Test
    void isUsedUpReturnsTrueWhenZeroLimit() {
        assertThat(couponView(0, 0, List.of()).isUsedUp()).isTrue();
    }

    @Test
    void allowedCountryCodesIsImmutableCopy() {
        var mutable = new ArrayList<>(List.of("US"));
        var view = couponView(10, 0, mutable);
        mutable.add("DE");
        assertThat(view.allowedCountryCodes()).containsExactly("US");
    }

    @Test
    void returnedAllowedCountryCodesIsUnmodifiable() {
        var view = couponView(10, 0, List.of("US"));
        assertThatThrownBy(() -> view.allowedCountryCodes().add("DE"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
