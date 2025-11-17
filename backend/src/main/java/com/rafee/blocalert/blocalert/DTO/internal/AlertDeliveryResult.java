package com.rafee.blocalert.blocalert.DTO.internal;

import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;

import java.time.LocalDateTime;

public record AlertDeliveryResult(
        Long alertId,
        AlertChannelStatus status,
        LocalDateTime triggeredAt
) {
}
