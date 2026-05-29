package com.github.tubicz.coupon_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api")
record ApiKeyProperties(String key) {
}
