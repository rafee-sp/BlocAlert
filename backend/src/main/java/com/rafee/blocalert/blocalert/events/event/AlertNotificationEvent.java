package com.rafee.blocalert.blocalert.events.event;

import com.rafee.blocalert.blocalert.DTO.internal.UserAlertNotification;

import java.util.List;

public record AlertNotificationEvent (List<UserAlertNotification> alertList) {
}
