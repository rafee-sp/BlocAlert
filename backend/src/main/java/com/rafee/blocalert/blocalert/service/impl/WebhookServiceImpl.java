package com.rafee.blocalert.blocalert.service.impl;

import com.rafee.blocalert.blocalert.entity.WebhookEvent;
import com.rafee.blocalert.blocalert.repository.WebhookEventRepository;
import com.rafee.blocalert.blocalert.service.WebhookEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookEventService {

    private final WebhookEventRepository webhookEventRepository;

    @Override
    public void recordWebhookEvent(String eventId, String eventType) {

        log.debug("recordWebhookEvent called for eventId {}, eventType {}", eventId, eventType);

        webhookEventRepository.save(
                WebhookEvent.builder()
                        .eventId(eventId)
                        .eventType(eventType)
                        .isProcessed(true)
                        .build()
        );

    }

    @Override
    public boolean existsByEventId(String eventId) {

        if(!StringUtils.hasText(eventId)) throw new IllegalArgumentException("Event Id is not valid " + eventId);

        return webhookEventRepository.existsByEventId(eventId);

    }
}
