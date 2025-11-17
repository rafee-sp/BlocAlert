package com.rafee.blocalert.blocalert.events.listener;

import com.rafee.blocalert.blocalert.events.event.UserEmailEvent;
import com.rafee.blocalert.blocalert.events.event.enums.EmailEventType;
import com.rafee.blocalert.blocalert.events.event.SubscriptionEmailEvent;
import com.rafee.blocalert.blocalert.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailService emailService;

    @Async
    @EventListener
    public void handleUserEmailEvent(UserEmailEvent event) {
        try {
            if (event.emailEventType() == EmailEventType.MAIL_VERIFICATION) {
                emailService.sendEmailVerification(event);
            } else if (event.emailEventType() == EmailEventType.PASSWORD_RESET) {
                emailService.sendResetPassword(event);
            } else {
                log.error("Unhandled event type in handleUserEmailEvent listener {}", event.emailEventType());
            }
        } catch (Exception e) {
            log.error("Failed to publish user email event {} - {}", event.userId(), event.emailEventType(), e);
        }
    }

    @Async
    @EventListener
    public void handleSubscriptionEmailEvent(SubscriptionEmailEvent event) {

        try {

            emailService.sendSubscriptionMail(event);

        } catch (Exception e) {
            log.error("Failed to publish subscription email event {} - {}", event.subscription().getId(), event.template(), e);
        }
    }
}
