package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import com.rafee.blocalert.blocalert.events.event.SubscriptionEmailEvent;
import com.rafee.blocalert.blocalert.events.event.UserEmailEvent;

public interface EmailService {

    void sendEmailAlerts(AlertNotificationEvent event);

    void sendEmailVerification(UserEmailEvent event) throws Exception;

    void sendResetPassword(UserEmailEvent event) throws Exception;

    void sendSubscriptionMail(SubscriptionEmailEvent event) throws Exception;
}
