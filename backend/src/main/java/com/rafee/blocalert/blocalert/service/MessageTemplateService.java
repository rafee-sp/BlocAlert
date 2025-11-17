package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.entity.MessageTemplate;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;

public interface MessageTemplateService {

    MessageTemplate getTemplate(AlertChannel channel, String templateCode);
}
