package com.rafee.blocalert.blocalert.events.event;

import com.rafee.blocalert.blocalert.entity.Subscription;
import com.rafee.blocalert.blocalert.entity.User;

public record SubscriptionEmailEvent(Subscription subscription, User user, String template) {
}
