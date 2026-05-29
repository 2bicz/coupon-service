package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.domain.command.Coupon;
import com.github.tubicz.coupon_service.domain.command.CouponRedemption;
import com.github.tubicz.coupon_service.domain.exception.CouponAlreadyRedeemedByUserException;
import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CouponRedemptionUseCase;
import com.github.tubicz.coupon_service.application.port.in.RedeemCouponCommand;
import com.github.tubicz.coupon_service.application.port.out.CouponRedemptionRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.ExternalPartyRepositoryPort;
import com.github.tubicz.coupon_service.application.port.out.IpGeolocationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
class CouponRedemptionService implements CouponRedemptionUseCase {
    private final CouponRepositoryPort couponRepository;
    private final IpGeolocationPort ipGeolocation;
    private final ExternalPartyRepositoryPort externalPartyRepository;
    private final CouponRedemptionRepositoryPort redemptionRepository;

    @Override
    @Transactional
    public void redeem(RedeemCouponCommand command) {
        String countryCode = ipGeolocation.getCountryCode(command.ipAddress());
        Coupon coupon = getLockedCoupon(command.couponCode());

        coupon.validateEligibility(
                countCurrentRedemptionsOfCoupon(coupon.id()),
                countryCode
        );

        var externalUserId = externalPartyRepository.findOrCreateExternalUserId(command.externalSystem(), command.externalUser());
        assertCouponNotYetRedeemedByUser(coupon, externalUserId);

        redemptionRepository.save(new CouponRedemption(coupon.id(), externalUserId, Instant.now()));
    }

    private Coupon getLockedCoupon(String couponCode) {
        return couponRepository.getByCodeWithLock(couponCode).orElseThrow(
                () -> new CouponNotFoundException("Coupon with code %s could not be found".formatted(couponCode))
        );
    }

    private int countCurrentRedemptionsOfCoupon(String couponId) {
        return redemptionRepository.countByCouponId(couponId);
    }

    private void assertCouponNotYetRedeemedByUser(Coupon coupon, String externalUserId) {
        if (redemptionRepository.existsByCouponIdAndUserId(coupon.id(), externalUserId)) {
            throw new CouponAlreadyRedeemedByUserException(coupon.code(), externalUserId);
        }
    }
}
