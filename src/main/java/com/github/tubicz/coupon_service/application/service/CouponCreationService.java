package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.AlreadyExistingCouponCodeException;
import com.github.tubicz.coupon_service.application.exception.CountryNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CouponCreationUseCase;
import com.github.tubicz.coupon_service.application.port.in.CreateCouponCommand;
import com.github.tubicz.coupon_service.application.port.out.CountryRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.command.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
class CouponCreationService implements CouponCreationUseCase {
    private final CouponRepositoryPort couponRepositoryPort;
    private final CountryRepositoryPort countryRepository;

    @Override
    public String create(CreateCouponCommand command) {
        assertCouponCodeNotExists(command.code());
        assertAllCountriesExist(command.allowedCountryCodes());

        var coupon = new Coupon(command.code(), command.usageLimit(), command.allowedCountryCodes());
        return couponRepositoryPort.create(coupon);
    }

    private void assertAllCountriesExist(Collection<String> countries) {
        Set<String> unknownCountryCodes = countryRepository.findUnknownCountryCodes(countries);
        if (!unknownCountryCodes.isEmpty()) throw new CountryNotFoundException(unknownCountryCodes);
    }

    private void assertCouponCodeNotExists(String code) {
        if (couponRepositoryPort.existsByCodeCaseInsensitive(code)) {
            throw new AlreadyExistingCouponCodeException(code);
        }
    }
}