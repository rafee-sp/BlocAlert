package com.rafee.blocalert.blocalert.repository;

import com.rafee.blocalert.blocalert.entity.MessageTemplate;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    Optional<MessageTemplate> findByChannelAndCode(AlertChannel channel, String templateCode);
}
