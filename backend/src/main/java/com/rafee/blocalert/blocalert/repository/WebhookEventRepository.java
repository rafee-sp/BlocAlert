package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
    boolean existsByEventId(String id);
}
