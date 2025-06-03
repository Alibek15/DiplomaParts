package com.example.paymentService.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "halyk")
@Data
public class HalykProps {
    private String baseUrl;
    private String oauthUrl;
    private String clientId;
    private String clientSecret;
    private String shopId;
    private String accountId;
    private String successUrl;
    private String failureUrl;
}
