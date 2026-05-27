package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

interface CountryJpaRepository extends JpaRepository<CountryJpaEntity, String> {
    @Query("SELECT c.code FROM CountryJpaEntity c WHERE c.code IN :codes")
    Set<String> findExistingCodesAmong(@Param("codes") Collection<String> codes);
}
