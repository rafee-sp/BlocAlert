package com.rafee.blocalert.blocalert.DTO.response;

import com.rafee.blocalert.blocalert.entity.enums.SubscriptionStatus;

import java.time.LocalDateTime;

public record SubscriptionDetailResponse(
        Long id,
        SubscriptionStatus subscriptionStatus,
        LocalDateTime currentSubscriptionEnd
) {
}
