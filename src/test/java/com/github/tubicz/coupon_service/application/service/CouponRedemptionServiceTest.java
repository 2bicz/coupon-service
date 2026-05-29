package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.exception.IpNotResolvableException;
import com.github.tubicz.coupon_service.application.port.in.RedeemCouponCommand;
import com.github.tubicz.coupon_service.application.port.out.CouponRedemptionRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.ExternalPartyRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.IpGeolocationPort;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.command.CouponRedemption;
import com.github.tubicz.coupon_service.domain.exception.CouponAlreadyRedeemedByUserException;
import com.github.tubicz.coupon_service.domain.exception.CouponExhaustedException;
import com.github.tubicz.coupon_service.domain.exception.CouponNotEligibleForCountryException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponRedemptionServiceTest {

    @Mock
    CouponRepositoryPort couponRepository;
    @Mock
    IpGeolocationPort ipGeolocation;
    @Mock
    ExternalPartyRepositoryPort externalPartyRepository;
    @Mock
    CouponRedemptionRepositoryPort redemptionRepository;

    @InjectMocks
    CouponRedemptionService service;

    private static final String COUPON_ID = "coupon-uuid";
    private static final Coupon COUPON = new Coupon(COUPON_ID, "CODE", 10, List.of("US"), Instant.now());
    private static final RedeemCouponCommand COMMAND =
            new RedeemCouponCommand("CODE", "user1", "system1", "1.2.3.4");

    @Test
    void redeemSavesRedemptionOnHappyPath() {
        when(ipGeolocation.getCountryCode("1.2.3.4")).thenReturn("US");
        when(couponRepository.getByCodeWithLock("CODE")).thenReturn(Optional.of(COUPON));
        when(redemptionRepository.countByCouponId(COUPON_ID)).thenReturn(0);
        when(externalPartyRepository.findOrCreateExternalUserId("system1", "user1")).thenReturn("user-uuid");
        when(redemptionRepository.existsByCouponIdAndUserId(COUPON_ID, "user-uuid")).thenReturn(false);

        assertThatCode(() -> service.redeem(COMMAND)).doesNotThrowAnyException();

        ArgumentCaptor<CouponRedemption> captor = ArgumentCaptor.forClass(CouponRedemption.class);
        verify(redemptionRepository).save(captor.capture());
        CouponRedemption saved = captor.getValue();
        assertThat(saved.couponId()).isEqualTo(COUPON_ID);
        assertThat(saved.externalUserId()).isEqualTo("user-uuid");
        assertThat(saved.createdAt()).isNotNull();
    }

    @Test
    void redeemThrowsWhenIpNotResolvable() {
        when(ipGeolocation.getCountryCode(anyString())).thenThrow(new IpNotResolvableException("1.2.3.4"));

        assertThatThrownBy(() -> service.redeem(COMMAND))
                .isInstanceOf(IpNotResolvableException.class);

        verify(couponRepository, never()).getByCodeWithLock(any());
    }

    @Test
    void redeemThrowsWhenCouponNotFound() {
        when(ipGeolocation.getCountryCode(anyString())).thenReturn("US");
        when(couponRepository.getByCodeWithLock("CODE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.redeem(COMMAND))
                .isInstanceOf(CouponNotFoundException.class);

        verify(redemptionRepository, never()).save(any());
    }

    @Test
    void redeemThrowsWhenCouponExhausted() {
        when(ipGeolocation.getCountryCode(anyString())).thenReturn("US");
        when(couponRepository.getByCodeWithLock("CODE")).thenReturn(Optional.of(COUPON));
        when(redemptionRepository.countByCouponId(COUPON_ID)).thenReturn(10);

        assertThatThrownBy(() -> service.redeem(COMMAND))
                .isInstanceOf(CouponExhaustedException.class);

        verify(redemptionRepository, never()).save(any());
    }

    @Test
    void redeemThrowsWhenCountryNotEligible() {
        when(ipGeolocation.getCountryCode(anyString())).thenReturn("DE");
        when(couponRepository.getByCodeWithLock("CODE")).thenReturn(Optional.of(COUPON));
        when(redemptionRepository.countByCouponId(COUPON_ID)).thenReturn(0);

        assertThatThrownBy(() -> service.redeem(COMMAND))
                .isInstanceOf(CouponNotEligibleForCountryException.class);

        verify(redemptionRepository, never()).save(any());
    }

    @Test
    void redeemThrowsWhenAlreadyRedeemedByUser() {
        when(ipGeolocation.getCountryCode(anyString())).thenReturn("US");
        when(couponRepository.getByCodeWithLock("CODE")).thenReturn(Optional.of(COUPON));
        when(redemptionRepository.countByCouponId(COUPON_ID)).thenReturn(0);
        when(externalPartyRepository.findOrCreateExternalUserId("system1", "user1")).thenReturn("user-uuid");
        when(redemptionRepository.existsByCouponIdAndUserId(COUPON_ID, "user-uuid")).thenReturn(true);

        assertThatThrownBy(() -> service.redeem(COMMAND))
                .isInstanceOf(CouponAlreadyRedeemedByUserException.class);

        verify(redemptionRepository, never()).save(any());
    }
}
