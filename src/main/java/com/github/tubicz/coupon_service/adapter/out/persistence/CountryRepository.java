package com.github.tubicz.coupon_service.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

interface CountryRepository extends JpaRepository<CountryEntity, String> {
    @Query("SELECT c.code FROM CountryEntity c WHERE c.code IN :codes")
    Set<String> findExistingCodesAmong(@Param("codes") Collection<String> codes);
}
