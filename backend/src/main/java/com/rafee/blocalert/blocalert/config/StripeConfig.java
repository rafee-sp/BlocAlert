package com.rafee.blocalert.blocalert.config;

import com.stripe.Stripe;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "stripe")
@Validated
@Setter
@Slf4j
public class StripeConfig {

    @Autowired
    private AppConfig appConfig;

    @NotBlank(message = "Stripe API secret key is not configured")
    private String secretKey;

    @NotBlank(message = "Stripe webhook secret is not configured")
    @Getter
    private String webhookSecret;

    @NotBlank(message = "Stripe Price Id is not configured")
    @Getter
    private String priceId;

    @EventListener(ApplicationReadyEvent.class)
    public void stripeInit() {

        Stripe.apiKey = secretKey;

        log.info("Stripe initialized successfully");

    }

    public String getSuccessUrl() {
        return appConfig.getFrontendUrl() + "/subscription/success?session_id={CHECKOUT_SESSION_ID}";
    }

    public String getCancelUrl() {
        return appConfig.getFrontendUrl() + "/subscription/cancel";
    }
}
