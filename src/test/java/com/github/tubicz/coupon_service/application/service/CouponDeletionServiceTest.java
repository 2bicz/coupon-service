package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponDeletionServiceTest {

    @Mock
    CouponRepositoryPort couponRepositoryPort;

    @InjectMocks
    CouponDeletionService service;

    @Test
    void deleteCallsRepositoryWhenCouponExists() {
        when(couponRepositoryPort.existsById("some-id")).thenReturn(true);

        assertThatCode(() -> service.delete("some-id")).doesNotThrowAnyException();

        verify(couponRepositoryPort).deleteById("some-id");
    }

    @Test
    void deleteThrowsWhenCouponNotFound() {
        when(couponRepositoryPort.existsById("missing-id")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("missing-id"))
                .isInstanceOf(CouponNotFoundException.class);

        verify(couponRepositoryPort, never()).deleteById(any());
    }
}
