package com.github.tubicz.coupon_service.adapter.out.persistence;

import com.github.tubicz.coupon_service.application.port.in.GetCouponsQuery;
import com.github.tubicz.coupon_service.application.port.out.CouponViewRepositoryPort;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponQueryAdapter implements CouponViewRepositoryPort {
    private final CouponViewRepository read;

    @Override
    public Optional<CouponView> getById(String id) {
        return read.findById(UUID.fromString(id)).map(this::toDomainView);
    }

    @Override
    public CouponPage getAll(GetCouponsQuery query) {
        var spec = Specification
                .where(CouponViewSpecification.hasCodeLike(query.search()))
                .and(CouponViewSpecification.createdAtFrom(query.createdAtFrom()))
                .and(CouponViewSpecification.createdAtTo(query.createdAtTo()));

        var pageable = PageRequest.of(query.page(), query.size(), Sort.by(Sort.Direction.DESC, "createdAt"));
        var page = read.findAll(spec, pageable);

        return new CouponPage(
                page.getContent().stream().map(this::toDomainView).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    private CouponView toDomainView(CouponViewEntity entity) {
        return new CouponView(
                entity.getId().toString(),
                entity.getCode(),
                entity.getCreatedAt(),
                entity.getUsageLimit(),
                entity.getUsageCount(),
                entity.getCountryCodes()
        );
    }
}
