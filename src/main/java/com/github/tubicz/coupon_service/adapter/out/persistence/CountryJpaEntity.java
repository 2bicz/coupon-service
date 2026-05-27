package com.github.tubicz.coupon_service.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "country")
class CountryJpaEntity {
    @Id
    @Column(name = "code", columnDefinition = "char(2)", length = 2, nullable = false, updatable = false)
    private String code;
}
