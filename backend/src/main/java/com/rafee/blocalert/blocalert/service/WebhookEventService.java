package com.rafee.blocalert.blocalert.service;

public interface WebhookEventService {

    void recordWebhookEvent(String eventId, String eventType);

    boolean existsByEventId(String eventId);
}
