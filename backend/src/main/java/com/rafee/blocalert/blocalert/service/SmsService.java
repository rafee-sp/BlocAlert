package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface SmsService {

    void sendSmsAlerts(AlertNotificationEvent event);

    boolean isValidTwilioResponse(Map<String, String> paramsMap, String signature, HttpServletRequest request);

    void handleSmsMessageCallback(String alertId, Map<String, String> paramsMap);
}
