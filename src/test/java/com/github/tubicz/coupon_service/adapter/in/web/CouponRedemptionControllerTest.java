package com.github.tubicz.coupon_service.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tubicz.coupon_service.application.exception.CouponNotFoundException;
import com.github.tubicz.coupon_service.application.exception.IpNotResolvableException;
import com.github.tubicz.coupon_service.application.port.in.CouponRedemptionUseCase;
import com.github.tubicz.coupon_service.domain.exception.CouponAlreadyRedeemedByUserException;
import com.github.tubicz.coupon_service.domain.exception.CouponExhaustedException;
import com.github.tubicz.coupon_service.domain.exception.CouponNotEligibleForCountryException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponRedemptionController.class)
@WithMockUser
class CouponRedemptionControllerTest {

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    CouponRedemptionUseCase couponRedemptionUseCase;

    @Test
    void redeemCouponReturns201() throws Exception {
        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "user1", "system1"))))
                .andExpect(status().isCreated());
    }

    @Test
    void redeemCouponWithBlankCodeReturns400() throws Exception {
        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("", "user1", "system1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void redeemCouponWithBlankUserReturns400() throws Exception {
        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "", "system1"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void redeemCouponWithBlankSystemReturns400() throws Exception {
        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "user1", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INCORRECT_REQUEST_BODY"));
    }

    @Test
    void redeemCouponReturns404WhenCouponNotFound() throws Exception {
        doThrow(new CouponNotFoundException("not found")).when(couponRedemptionUseCase).redeem(any());

        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("MISSING", "user1", "system1"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("COUPON_NOT_FOUND"));
    }

    @Test
    void redeemCouponReturns409WhenExhausted() throws Exception {
        doThrow(new CouponExhaustedException("CODE")).when(couponRedemptionUseCase).redeem(any());

        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "user1", "system1"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("COUPON_EXHAUSTED"));
    }

    @Test
    void redeemCouponReturns409WhenAlreadyRedeemed() throws Exception {
        doThrow(new CouponAlreadyRedeemedByUserException("CODE", "user1")).when(couponRedemptionUseCase).redeem(any());

        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "user1", "system1"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("COUPON_ALREADY_REDEEMED"));
    }

    @Test
    void redeemCouponReturns403WhenCountryNotEligible() throws Exception {
        doThrow(new CouponNotEligibleForCountryException("CODE", "CN")).when(couponRedemptionUseCase).redeem(any());

        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "user1", "system1"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("COUPON_NOT_ELIGIBLE_FOR_COUNTRY"));
    }

    @Test
    void redeemCouponReturns422WhenIpNotResolvable() throws Exception {
        doThrow(new IpNotResolvableException("127.0.0.1")).when(couponRedemptionUseCase).redeem(any());

        mockMvc.perform(post("/coupon-redemption").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateCouponRedemptionRequestBody("CODE", "user1", "system1"))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value("IP_NOT_RESOLVABLE"));
    }
}
