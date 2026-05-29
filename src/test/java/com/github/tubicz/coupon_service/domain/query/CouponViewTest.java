package com.github.tubicz.coupon_service.domain.query;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponViewTest {

    private CouponView couponView(List<String> countries) {
        return new CouponView(UUID.randomUUID().toString(), "CODE", Instant.now(), 10, 0, countries);
    }

    @Test
    void allowedCountryCodesIsImmutableCopy() {
        var mutable = new ArrayList<>(List.of("US"));
        var view = couponView(mutable);
        mutable.add("DE");
        assertThat(view.allowedCountryCodes()).containsExactly("US");
    }

    @Test
    void returnedAllowedCountryCodesIsUnmodifiable() {
        var view = couponView(List.of("US"));
        assertThatThrownBy(() -> view.allowedCountryCodes().add("DE"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
