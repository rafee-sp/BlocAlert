package com.rafee.blocalert.blocalert.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.rafee.blocalert.blocalert.entity.MessageTemplate;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.exception.ResourceNotFoundException;
import com.rafee.blocalert.blocalert.repository.MessageTemplateRepository;
import com.rafee.blocalert.blocalert.service.MessageTemplateService;
import com.rafee.blocalert.blocalert.service.RedisService;
import com.rafee.blocalert.blocalert.utils.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageTemplateServiceImpl implements MessageTemplateService {

    private final MessageTemplateRepository messageTemplateRepository;
    private final RedisService redisService;

    @Override
    public MessageTemplate getTemplate(AlertChannel channel, String templateCode) {

        log.debug("MessageTemplate called for {} - {}", channel, templateCode);

        MessageTemplate cachedTemplate = redisService.valueGet(RedisKeys.templateKey(channel, templateCode), new TypeReference<MessageTemplate>() {
        });

        if (cachedTemplate != null) return cachedTemplate;

        MessageTemplate template = messageTemplateRepository.findByChannelAndCode(channel, templateCode)
                .orElseThrow(() -> new ResourceNotFoundException("Message Template not found for code: " + templateCode + " and channel: " + channel));

        redisService.valueSet(RedisKeys.templateKey(channel, templateCode), template);

        return template;

    }
}
