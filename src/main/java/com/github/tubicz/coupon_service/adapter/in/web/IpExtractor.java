package com.github.tubicz.coupon_service.adapter.in.web;

import jakarta.servlet.http.HttpServletRequest;

class IpExtractor {

    private IpExtractor() {}

    // X-Forwarded-For is set by proxies/load balancers; first value is the original client.
    // Note: only trust this header when the app runs behind a known trusted proxy.
    static String extract(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].strip();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) {
            return xri.strip();
        }
        return request.getRemoteAddr();
    }
}
