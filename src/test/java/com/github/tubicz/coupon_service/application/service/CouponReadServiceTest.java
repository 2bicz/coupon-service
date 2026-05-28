package com.github.tubicz.coupon_service.application.service;

import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.application.port.out.CouponRepositoryPort;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponReadServiceTest {

    @Mock
    CouponRepositoryPort couponRepository;

    @InjectMocks
    CouponReadService service;

    @Test
    void getCouponByIdReturnsCouponView() {
        var id = UUID.randomUUID();
        var view = new CouponView(id, "SUMMER20", Instant.now(), 10, 3, List.of("US"));
        when(couponRepository.getById(id.toString())).thenReturn(Optional.of(view));

        CouponView result = service.getCouponById(id.toString());

        assertThat(result).isEqualTo(view);
    }

    @Test
    void getCouponByIdThrowsWhenNotFound() {
        when(couponRepository.getById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCouponById("missing-id"))
                .isInstanceOf(CouponNotFoundException.class);
    }

    @Test
    void getAllReturnsCouponPage() {
        var query = new GetCouponsQuery(0, 10, null, null, null);
        var view = new CouponView(UUID.randomUUID(), "CODE", Instant.now(), 5, 1, List.of("DE"));
        var expected = new CouponPage(List.of(view), 0, 10, 1L);
        when(couponRepository.getAll(query)).thenReturn(expected);

        CouponPage result = service.getAll(query);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getAllDelegatesToRepositoryWithSameQuery() {
        var query = new GetCouponsQuery(2, 20, "promo", Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-12-31T23:59:59Z"));
        when(couponRepository.getAll(any())).thenReturn(new CouponPage(List.of(), 2, 20, 0L));

        service.getAll(query);

        var captor = ArgumentCaptor.forClass(GetCouponsQuery.class);
        verify(couponRepository).getAll(captor.capture());
        assertThat(captor.getValue()).isEqualTo(query);
    }
}
