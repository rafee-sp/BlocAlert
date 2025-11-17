package com.rafee.blocalert.blocalert.controller;

import com.rafee.blocalert.blocalert.config.StripeConfig;
import com.rafee.blocalert.blocalert.service.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stripe")
@RequiredArgsConstructor
@Slf4j
public class StripeCallbackController {

    private final SubscriptionService subscriptionService;
    private final StripeConfig stripeConfig;

    @PostMapping("/callback")
    public ResponseEntity<String> getSessionDetailsCallback(@RequestBody String payload, @RequestHeader("Stripe-Signature") String stripeSignature) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, stripeSignature, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.error("exception occurred : ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            subscriptionService.handleStripeCallback(event);
            return ResponseEntity.ok().body("Success");
        } catch (Exception e) {
            log.error("Error handling stripe webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
