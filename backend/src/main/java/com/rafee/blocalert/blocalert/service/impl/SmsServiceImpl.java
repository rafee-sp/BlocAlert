package com.rafee.blocalert.blocalert.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.rafee.blocalert.blocalert.DTO.internal.AlertDeliveryResult;
import com.rafee.blocalert.blocalert.DTO.internal.AlertVisuals;
import com.rafee.blocalert.blocalert.DTO.internal.UserContactInfo;
import com.rafee.blocalert.blocalert.config.SmsConfig;
import com.rafee.blocalert.blocalert.entity.MessageTemplate;
import com.rafee.blocalert.blocalert.entity.SmsLog;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import com.rafee.blocalert.blocalert.DTO.internal.UserAlertNotification;
import com.rafee.blocalert.blocalert.exception.AlertDeliveryException;
import com.rafee.blocalert.blocalert.service.*;
import com.rafee.blocalert.blocalert.utils.Utils;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.security.RequestValidator;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    private final UserService userService;
    private final MessageTemplateService messageTemplateService;
    private final AlertDeliveryService alertDeliveryService;
    private final SmsLogService smsLogService;
    private final SmsConfig smsConfig;
    private volatile RateLimiter smsRateLimiter;

    @PostConstruct
    public void init() {
        smsRateLimiter = RateLimiter.create(smsConfig.getRateLimit());
    }

    @KafkaListener(topics = "sms-alerts", groupId = "blocalert-group")
    @Override
    public void sendSmsAlerts(AlertNotificationEvent event) {

        log.info("sendSmsAlerts called for {}", event.alertList().size());

        try {

            List<UserAlertNotification> alerts = event.alertList();
            MessageTemplate messageTemplate = messageTemplateService.getTemplate(AlertChannel.SMS, "SMS_ALERT");

            Set<Long> userIds = alerts.stream()
                    .map(UserAlertNotification::getUserId)
                    .collect(Collectors.toSet());

            Map<Long, UserContactInfo> usersContactMap = userService.getUsersContactInfo(userIds);

            String templateContent = messageTemplate.getContent();

            List<SmsLog> smsLogs = new ArrayList<>();

            String callbackUrl = smsConfig.getCallbackUrl();

            for (UserAlertNotification alert : alerts) {
                String message = null, messageUUID = null, messageCallbackUrl ;
                AlertChannelStatus status;
                try {

                    UserContactInfo userContactInfo = usersContactMap.get(alert.getUserId());
                    messageCallbackUrl = callbackUrl+ "?alertId="+alert.getAlertId();

                    if (!userContactInfo.isSubscribed()) {
                        log.warn("User not subscribed");
                        continue;
                    }

                    String userPhoneNumber = userContactInfo.phoneNumber();
                    if (!StringUtils.hasText(userPhoneNumber)) {
                        log.warn("User has invalid phone number");
                        continue;
                    }

                    userPhoneNumber = Utils.formatPhoneNumber(userPhoneNumber, smsConfig.getRegion());


                    AlertVisuals alertVisuals = AlertVisuals.getVisual(alert.getAlertCondition());
                    String thresholdPrice = Utils.formatPrice(alert.getThresholdValue());
                    String currentPrice = Utils.formatPrice(alert.getCurrentPrice());

                    message = templateContent
                            .replace("${emoji}", alertVisuals.getEmoji())
                            .replace("${alertConditionText}", alertVisuals.getText())
                            .replace("${cryptoName}", alert.getCryptoName())
                            .replace("${thresholdValue}", thresholdPrice)
                            .replace("${currentPrice}", currentPrice)
                            .replace("${timestamp}", Utils.getFormattedCurrDate());

                    messageUUID = sendSms(alert.getAlertId(), userPhoneNumber, message, messageCallbackUrl);
                    status = AlertChannelStatus.PENDING;


                } catch (Exception e) {
                    log.error("Failed to send email alert for user {} (alertId={})", alert.getUserId(), alert.getAlertId(), e);
                    status = AlertChannelStatus.FAILED;
                }

                alertDeliveryService.recordAlertDelivery(AlertChannel.SMS, new AlertDeliveryResult(alert.getAlertId(), status, LocalDateTime.now()));
                smsLogs.add(buildSmsLog(alert.getUserId(), alert.getAlertId(), message, messageUUID));
            }

            smsLogService.saveLogs(smsLogs);

        } catch (
                Exception e) {
            log.error("Error occurred while sending sms Alerts", e);
        }
    }

    @Override
    public boolean isValidTwilioResponse(Map<String, String> paramsMap, String signature, HttpServletRequest request) {

        log.info("isValidTwilioResponse called");

        try {

            if (signature == null || signature.isEmpty()) {
                log.warn("Empty signature");
                return false;
            }

            String scheme = Optional.ofNullable(request.getHeader("X-Forwarded-Proto"))
                    .orElse(request.getScheme());

            String host = Optional.ofNullable(request.getHeader("X-Forwarded-Host"))
                    .orElse(request.getServerName());

            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(scheme).append("://").append(host).append(request.getRequestURI());

            if (request.getQueryString() != null) {
                urlBuilder.append("?").append(request.getQueryString());
            }

            String fullUrl = urlBuilder.toString();

            RequestValidator validator = new RequestValidator(smsConfig.getAuthId());

            return validator.validate(fullUrl, paramsMap, signature);

        } catch (Exception ex) {
            log.error("Error validating Twilio/Plivo signature", ex);
            return false;
        }

    }

    @Override
    public void handleSmsMessageCallback(String alertIdStr, Map<String, String> paramsMap) {

        log.debug("handleSmsMessageCallback called {}", alertIdStr);

        if (!StringUtils.hasText(alertIdStr)) {
            return;
        }

        Long alertId = Long.valueOf(alertIdStr);

        if (paramsMap == null || paramsMap.isEmpty()) {
            log.error("Twilio sends response with missing parameters");
            return;
        }

        String messageSID = paramsMap.get("MessageSid");
        String status = paramsMap.get("MessageStatus");

        log.debug("MessageSid : {}", messageSID);
        log.debug("status : {}", status);

        if (!StringUtils.hasText(messageSID) || !StringUtils.hasText(status)) {
            log.error("No messageId or status present in the Text callback messageId - {} status - {}", messageSID, status);
            return;
        }

        if (!status.equalsIgnoreCase("delivered")) {
            return;
        }

        try {

            alertDeliveryService.updateDeliveryStatus(alertId, AlertChannelStatus.DELIVERED);

        } catch (Exception e) {
            log.error("Failed to update message status for MessageSid: {}, status: {}, alertId {}", messageSID, status, alertId, e);
        }
    }

    private String sendSms(Long alertId, String userPhoneNumber, String content, String callbackUrl) {

        smsRateLimiter.acquire();

        Message message = Message.creator(new PhoneNumber(userPhoneNumber),
                        new PhoneNumber(smsConfig.getPhone()),
                        content)
                .setStatusCallback(URI.create(callbackUrl))
                .create();

        if (!StringUtils.hasText(message.getSid())) {
            throw new AlertDeliveryException("Message not sent for alert "+alertId);
        }

        return message.getSid();

    }

    private SmsLog buildSmsLog(Long userId, Long alertId, String message, String messageUUID) {
        return SmsLog.builder()
                .alertId(alertId)
                .userId(userId)
                .content(message)
                .messageUUId(messageUUID)
                .build();
    }
}
