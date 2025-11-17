package com.rafee.blocalert.blocalert.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mail")
@Data
public class MailConfig {

    private String from;
    private String name;
    private double rateLimit;

}
