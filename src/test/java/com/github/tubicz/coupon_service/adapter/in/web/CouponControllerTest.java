package com.github.tubicz.coupon_service.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tubicz.coupon_service.application.exception.AlreadyExistingCouponCodeException;
import com.github.tubicz.coupon_service.application.exception.CountryNotFoundException;
import com.github.tubicz.coupon_service.application.exception.CouponHasRedemptionsException;
import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.port.in.CouponCreationUseCase;
import com.github.tubicz.coupon_service.application.port.in.CouponDeletionUseCase;
import com.github.tubicz.coupon_service.application.port.in.CouponReadUseCase;
import com.github.tubicz.coupon_service.domain.query.CouponPage;
import com.github.tubicz.coupon_service.domain.query.CouponView;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
@WithMockUser
class CouponControllerTest {

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    CouponCreationUseCase couponCreationUseCase;

    @MockitoBean
    CouponReadUseCase couponReadUseCase;

    @MockitoBean
    CouponDeletionUseCase couponDeletionUseCase;

    @Test
    void createCouponReturns201WithLocationHeader() throws Exception {
        when(couponCreationUseCase.create(any())).thenReturn("test-uuid");

        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRequestBody("SUMMER20", 10, List.of("US", "DE")))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/coupon/test-uuid")));
    }

    @Test
    void createCouponWithBlankCodeReturns400() throws Exception {
        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRequestBody("", 10, List.of("US")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void createCouponWithNullCodeReturns400() throws Exception {
        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\":null,\"usageLimit\":10,\"countryCodes\":[\"US\"]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void createCouponWithEmptyCountryCodesReturns400() throws Exception {
        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRequestBody("CODE", 10, List.of()))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void createCouponWithInvalidCountryCodeFormatReturns400() throws Exception {
        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRequestBody("CODE", 10, List.of("us")))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void createCouponWithExistingCodeReturns409() throws Exception {
        when(couponCreationUseCase.create(any()))
                .thenThrow(new AlreadyExistingCouponCodeException("TAKEN"));

        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRequestBody("TAKEN", 10, List.of("US")))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("COUPON_ALREADY_EXISTS"));
    }

    @Test
    void createCouponWithUnknownCountryReturns404() throws Exception {
        when(couponCreationUseCase.create(any()))
                .thenThrow(new CountryNotFoundException(Set.of("XX")));

        mockMvc.perform(post("/coupon").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRequestBody("CODE", 10, List.of("US")))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("COUNTRY_NOT_FOUND"));
    }

    // GET /coupon — list

    @Test
    void getListOfCouponsReturns200WithPagedContent() throws Exception {
        var view = new CouponView(UUID.randomUUID().toString(), "SUMMER20", Instant.parse("2024-06-01T00:00:00Z"), 10, 3, List.of("US"));
        when(couponReadUseCase.getAll(any())).thenReturn(new CouponPage(List.of(view), 0, 10, 1L));

        mockMvc.perform(get("/coupon").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("SUMMER20"))
                .andExpect(jsonPath("$.content[0].usageLimit").value(10))
                .andExpect(jsonPath("$.content[0].usageCount").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.first").value(notNullValue()))
                .andExpect(jsonPath("$.last").value(notNullValue()))
                .andExpect(jsonPath("$.previous").value(nullValue()))
                .andExpect(jsonPath("$.next").value(nullValue()));
    }

    @Test
    void getListOfCouponsFirstPageHasNoPreviousLink() throws Exception {
        when(couponReadUseCase.getAll(any())).thenReturn(new CouponPage(List.of(), 0, 10, 25L));

        mockMvc.perform(get("/coupon").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.previous").value(nullValue()))
                .andExpect(jsonPath("$.next").value(notNullValue()));
    }

    @Test
    void getListOfCouponsLastPageHasNoNextLink() throws Exception {
        when(couponReadUseCase.getAll(any())).thenReturn(new CouponPage(List.of(), 2, 10, 25L));

        mockMvc.perform(get("/coupon").param("page", "2").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.next").value(nullValue()))
                .andExpect(jsonPath("$.previous").value(notNullValue()));
    }

    @Test
    void getListOfCouponsWithNegativePageReturns400() throws Exception {
        mockMvc.perform(get("/coupon").param("page", "-1").param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void getListOfCouponsWithSizeZeroReturns400() throws Exception {
        mockMvc.perform(get("/coupon").param("page", "0").param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void getListOfCouponsWithSizeOverLimitReturns400() throws Exception {
        mockMvc.perform(get("/coupon").param("page", "0").param("size", "101"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void getListOfCouponsWithInvalidTimePeriodReturns400() throws Exception {
        mockMvc.perform(get("/coupon")
                        .param("page", "0")
                        .param("size", "10")
                        .param("createdAtFrom", "2024-06-01T00:00:00Z")
                        .param("createdAtTo", "2024-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void getCouponByIdReturns200WithCouponData() throws Exception {
        var id = UUID.randomUUID();
        var view = new CouponView(id.toString(), "PROMO", Instant.parse("2024-06-01T00:00:00Z"), 5, 1, List.of("DE"));
        when(couponReadUseCase.getCouponById(id.toString())).thenReturn(view);

        mockMvc.perform(get("/coupon/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("PROMO"))
                .andExpect(jsonPath("$.usageLimit").value(5))
                .andExpect(jsonPath("$.usageCount").value(1));
    }

    @Test
    void getCouponByIdReturns404WhenNotFound() throws Exception {
        when(couponReadUseCase.getCouponById(any()))
                .thenThrow(new CouponNotFoundException("not found"));

        mockMvc.perform(get("/coupon/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("COUPON_NOT_FOUND"));
    }

    // DELETE /coupon/{id}

    @Test
    void deleteCouponReturns204() throws Exception {
        mockMvc.perform(delete("/coupon/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCouponReturns404WhenNotFound() throws Exception {
        doThrow(new CouponNotFoundException("not found")).when(couponDeletionUseCase).delete(any());

        mockMvc.perform(delete("/coupon/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("COUPON_NOT_FOUND"));
    }

    @Test
    void deleteCouponReturns409WhenCouponHasRedemptions() throws Exception {
        doThrow(new CouponHasRedemptionsException("some-id")).when(couponDeletionUseCase).delete(any());

        mockMvc.perform(delete("/coupon/{id}", UUID.randomUUID()).with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("COUPON_HAS_REDEMPTIONS"));
    }
}
