package com.rafee.blocalert.blocalert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rafee.blocalert.blocalert.DTO.response.SubscriptionDetailResponse;
import com.rafee.blocalert.blocalert.DTO.response.SubscriptionResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

public interface SubscriptionService {

    String createSubscription(Long userId) throws StripeException;

    SubscriptionResponse getSubscriptionSessionDetails(Long userId, String sessionId) throws StripeException;

    void cancelSubscription(Long userId) throws StripeException;

    SubscriptionDetailResponse getUserSubscriptionDetails(Long userId);

    void handleStripeCallback(Event event) throws JsonProcessingException;

}
