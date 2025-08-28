// VapiConfig.java
package com.example.kitchen_assistant_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VapiConfig {

    @Value("${vapi.api.key}")
    private String secretKey;

    @Value("${vapi.api.key:}")
    public String getSecretKey() {
        return secretKey;
    }
}