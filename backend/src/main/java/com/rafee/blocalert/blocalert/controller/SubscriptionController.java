package com.rafee.blocalert.blocalert.controller;

import com.rafee.blocalert.blocalert.DTO.response.ApiResponse;
import com.rafee.blocalert.blocalert.DTO.response.SubscriptionDetailResponse;
import com.rafee.blocalert.blocalert.DTO.response.SubscriptionResponse;
import com.rafee.blocalert.blocalert.service.AuthenticationService;
import com.rafee.blocalert.blocalert.service.SubscriptionService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscription")
@RequiredArgsConstructor
@Slf4j
public class SubscriptionController {

    private final AuthenticationService authenticationService;
    private final SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    @PreAuthorize("hasRole('FREE_USER')")
    public ResponseEntity<ApiResponse> subscribeToPlan() throws StripeException {

        log.debug("subscribeToPlan called");

        Long userId = authenticationService.getCurrentUserId();

        String subscriptionUrl = subscriptionService.createSubscription(userId);

        return ResponseEntity.ok().body(new ApiResponse("Subscription Url created", subscriptionUrl));

    }

    @PostMapping("/cancel")
    @PreAuthorize("hasRole('PREMIUM_USER')")
    public ResponseEntity<ApiResponse> cancelSubscription() throws StripeException {

        log.debug("cancelSubscription called");

        Long userId = authenticationService.getCurrentUserId();

        subscriptionService.cancelSubscription(userId);

        return ResponseEntity.ok().body(new ApiResponse("Subscription Cancelled", null));

    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getSubscriptionDetails() {

        log.debug("getSubscriptionDetails called");

        Long userId = authenticationService.getCurrentUserId();
        SubscriptionDetailResponse subscriptionDetails = subscriptionService.getUserSubscriptionDetails(userId);
        return ResponseEntity.ok().body(new ApiResponse("Subscriptions fetched", subscriptionDetails));
    }

    @PostMapping("/session/verify")
    public ResponseEntity<ApiResponse> verifySession(@RequestBody Map<String,String> sessionMap) throws StripeException {

        log.debug("verifySession called : {}", sessionMap);

        Long userId = authenticationService.getCurrentUserId();

        SubscriptionResponse response = subscriptionService.getSubscriptionSessionDetails(userId, sessionMap.get("sessionId"));
        return ResponseEntity.ok().body(new ApiResponse("Session status fetched", response));

    }

}
