package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.AlreadyExistingCouponCodeException;
import com.github.tubicz.coupon_service.application.exception.CountryNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CreateCouponCommand;
import com.github.tubicz.coupon_service.application.port.out.CountryRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponCreationServiceTest {

    @Mock
    private CouponRepositoryPort couponRepositoryPort;

    @Mock
    private CountryRepositoryPort countryRepository;

    @InjectMocks
    private CouponCreationService service;

    @Test
    void createReturnsCouponId() {
        when(couponRepositoryPort.existsByCode("SUMMER20")).thenReturn(false);
        when(countryRepository.findUnknownCountryCodes(any())).thenReturn(Set.of());
        when(couponRepositoryPort.create(any())).thenReturn("generated-id");

        String id = service.create(new CreateCouponCommand("SUMMER20", 10, List.of("US")));

        assertThat(id).isEqualTo("generated-id");
    }

    @Test
    void createPassesCorrectCouponToRepository() {
        when(couponRepositoryPort.existsByCode(anyString())).thenReturn(false);
        when(countryRepository.findUnknownCountryCodes(any())).thenReturn(Set.of());
        when(couponRepositoryPort.create(any())).thenReturn("id");

        service.create(new CreateCouponCommand("summer20", 5, List.of("DE", "PL")));

        var captor = ArgumentCaptor.forClass(Coupon.class);
        verify(couponRepositoryPort).create(captor.capture());
        Coupon saved = captor.getValue();
        assertThat(saved.code()).isEqualTo("SUMMER20");
        assertThat(saved.usageLimit()).isEqualTo(5);
        assertThat(saved.allowedCountryCodes()).containsExactlyInAnyOrder("DE", "PL");
    }

    @Test
    void createThrowsWhenCodeAlreadyExists() {
        when(couponRepositoryPort.existsByCode("TAKEN")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateCouponCommand("TAKEN", 10, List.of("US"))))
                .isInstanceOf(AlreadyExistingCouponCodeException.class);

        verify(countryRepository, never()).findUnknownCountryCodes(any());
        verify(couponRepositoryPort, never()).create(any());
    }

    @Test
    void createThrowsWhenUnknownCountryCodes() {
        when(couponRepositoryPort.existsByCode(anyString())).thenReturn(false);
        when(countryRepository.findUnknownCountryCodes(any())).thenReturn(Set.of("XX", "ZZ"));

        assertThatThrownBy(() -> service.create(new CreateCouponCommand("CODE", 10, List.of("US", "XX", "ZZ"))))
                .isInstanceOf(CountryNotFoundException.class);

        verify(couponRepositoryPort, never()).create(any());
    }

    @Test
    void createChecksCodeExistenceBeforeCountries() {
        when(couponRepositoryPort.existsByCode("EXISTING")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateCouponCommand("EXISTING", 10, List.of("XX"))))
                .isInstanceOf(AlreadyExistingCouponCodeException.class);

        verifyNoInteractions(countryRepository);
    }
}
