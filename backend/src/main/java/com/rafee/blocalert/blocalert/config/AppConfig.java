package com.rafee.blocalert.blocalert.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application")
@Data
public class AppConfig {

    private int freeAlertLimit;
    private String frontendUrl;
    private String backendUrl;
}
