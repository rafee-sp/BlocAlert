package com.rafee.blocalert.blocalert.events.event;

import com.rafee.blocalert.blocalert.events.event.enums.EmailEventType;

public record UserEmailEvent(
        Long userId,
        EmailEventType emailEventType
){}
