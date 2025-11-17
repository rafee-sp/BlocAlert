package com.rafee.blocalert.blocalert.events.publisher;

import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import com.rafee.blocalert.blocalert.DTO.internal.UserAlertNotification;
import com.rafee.blocalert.blocalert.events.event.SendAlertEvent;
import com.rafee.blocalert.blocalert.service.AlertService;
import com.rafee.blocalert.blocalert.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AlertNotificationPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AlertService alertService;
    public AlertNotificationPublisher(KafkaTemplate<String, Object> kafkaTemplate, @Lazy AlertService alertService) {  // TODO : change to another event
        this.kafkaTemplate = kafkaTemplate;
        this.alertService = alertService;
    }

    public void publishAlertNotifications(SendAlertEvent sendAlertEvent) {

        log.info("publishAlertNotifications called");

        List<UserAlertNotification> alertNotificationList = alertService.evaluateAlerts(sendAlertEvent.alertEvent());

        if (alertNotificationList.isEmpty()) {
            log.info("No alerts to publish");
            return;
        }

        publishAlerts("websocket-alerts", alertNotificationList);

        List<UserAlertNotification> smsAlerts = alertNotificationList.stream()
                .filter(UserAlertNotification::isSmsSubscribed)
                .toList();

        if (!smsAlerts.isEmpty()) publishAlerts("sms-alerts", smsAlerts);

        List<UserAlertNotification> emailAlerts = alertNotificationList.stream()
                .filter(UserAlertNotification::isEmailSubscribed)
                .toList();

        if (!emailAlerts.isEmpty()) publishAlerts("email-alerts", emailAlerts);

    }

    private void publishAlerts(String topic, List<UserAlertNotification> alerts) {
        try {
            kafkaTemplate.send(topic, new AlertNotificationEvent(alerts));
            log.info("Published {} alerts to {}", alerts.size(), topic);
        } catch (Exception e) {
            log.error("Failed to publish to {}: {}", topic, e.getMessage(), e);
        }
    }

}
