package com.rafee.blocalert.blocalert.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.rafee.blocalert.blocalert.DTO.internal.*;
import com.rafee.blocalert.blocalert.config.AppConfig;
import com.rafee.blocalert.blocalert.config.MailConfig;
import com.rafee.blocalert.blocalert.entity.EmailLog;
import com.rafee.blocalert.blocalert.entity.MessageTemplate;
import com.rafee.blocalert.blocalert.entity.Subscription;
import com.rafee.blocalert.blocalert.entity.User;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import com.rafee.blocalert.blocalert.events.event.SubscriptionEmailEvent;
import com.rafee.blocalert.blocalert.events.event.UserEmailEvent;
import com.rafee.blocalert.blocalert.service.*;
import com.rafee.blocalert.blocalert.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MessageTemplateService messageTemplateService;
    private final UserService userService;
    private final AlertDeliveryService alertDeliveryService;
    private final EmailLogService emailLogService;
    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;
    private final AppConfig appConfig;
    private volatile RateLimiter mailRateLimiter;
    private final Auth0ManagementService auth0ManagementService;

    @PostConstruct
    public void init() {
        mailRateLimiter = RateLimiter.create(mailConfig.getRateLimit());
    }

    @KafkaListener(topics = "email-alerts", groupId = "blocalert-group")
    @Override
    public void sendEmailAlerts(AlertNotificationEvent event) {

        log.info("sendEmailAlerts called for {}", event.alertList().size());

        try {

            List<UserAlertNotification> alerts = event.alertList();
            MessageTemplate messageTemplate = getTemplate("EMAIL_ALERT");

            Set<Long> userIds = alerts.stream()
                    .map(UserAlertNotification::getUserId)
                    .collect(Collectors.toSet());

            Map<Long, UserContactInfo> usersContactMap = userService.getUsersContactInfo(userIds);

            String subject = messageTemplate.getSubject();
            String templateContent = messageTemplate.getContent();

            List<AlertDeliveryResult> deliveryResults = new ArrayList<>();
            List<EmailLog> emailLogs = new ArrayList<>();

            String frontendUrl = appConfig.getFrontendUrl();

            for (UserAlertNotification alert : alerts) {
                String message = "";
                AlertChannelStatus status;
                try {

                    UserContactInfo userContactInfo = usersContactMap.get(alert.getUserId());

                    if (!userContactInfo.isSubscribed()) {
                        log.warn("User not subscribed");
                        continue;
                    }

                    String userEmail = userContactInfo.email();
                    if (!StringUtils.hasText(userEmail)) {
                        log.warn("User has invalid email");
                        continue;
                    }

                    AlertVisuals alertVisuals = AlertVisuals.getVisual(alert.getAlertCondition());
                    String thresholdPrice = Utils.formatPrice(alert.getThresholdValue());
                    String currentPrice = Utils.formatPrice(alert.getCurrentPrice());

                    String updatedSubject = subject
                            .replace("${emoji}", alertVisuals.getEmoji())
                            .replace("${cryptoName}", alert.getCryptoName())
                            .replace("${alertConditionText}", alertVisuals.getText())
                            .replace("${thresholdValue}", thresholdPrice);

                    message = templateContent
                            .replace("${accentColor}", alertVisuals.getColor())
                            .replace("${emoji}", alertVisuals.getEmoji())
                            .replace("${alertConditionText}", alertVisuals.getText())
                            .replace("${alertConditionTextButton}", StringUtils.capitalize(alertVisuals.getText()))
                            .replace("${cryptoName}", alert.getCryptoName())
                            .replace("${cryptoImage}", alert.getCryptoImage())
                            .replace("${thresholdValue}", thresholdPrice)
                            .replace("${currentPrice}", currentPrice)
                            .replace("${timestamp}", Utils.getFormattedCurrDate())
                            .replace("${userId}", String.valueOf(alert.getUserId()))
                            .replace("${dashboardUrl}", frontendUrl);

                    sendMail(userEmail, updatedSubject, message);
                    status = AlertChannelStatus.DELIVERED;

                } catch (Exception e) {
                    log.error("Failed to send email alert for user {} (alertId={})", alert.getUserId(), alert.getAlertId(), e);
                    status = AlertChannelStatus.FAILED;
                }
                deliveryResults.add(new AlertDeliveryResult(alert.getAlertId(), status, LocalDateTime.now()));
                emailLogs.add(buildEmailLog(alert.getUserId(), alert.getAlertId(), message));
            }

            alertDeliveryService.recordAlertDeliveries(AlertChannel.EMAIL, deliveryResults);
            emailLogService.saveLogs(emailLogs);

        } catch (
                Exception e) {
            log.error("Error occurred while sending Email Alerts", e);
        }
    }

    @Override
    public void sendEmailVerification(UserEmailEvent event) throws Exception {

        log.info("sendEmailVerification mail called for {}", event.userId());

        User user = userService.getUser(event.userId());

        String verificationUrl = auth0ManagementService.getEmailVerificationLink(user.getAuth0Id());

        MessageTemplate messageTemplate = getTemplate("VERIFY_EMAIL");

        String subject = messageTemplate.getSubject();
        String content = messageTemplate.getContent();

        String updatedMessage = content
                .replace("${userName}", user.getName())
                .replace("${verificationUrl}", verificationUrl)
                .replace("${currentYear}", String.valueOf(Year.now().getValue()));

        sendMail(user.getEmail(), subject, updatedMessage);

        emailLogService.saveLog(buildEmailLog(user.getId(), null, updatedMessage));

    }

    @Override
    public void sendResetPassword(UserEmailEvent event) throws Exception {

        log.info("sendResetPassword mail called for {}", event.userId());

        User user = userService.getUser(event.userId());

        String resetUrl = auth0ManagementService.getResetPasswordLink(user.getAuth0Id());

        MessageTemplate messageTemplate = getTemplate("PASSWORD_RESET");

        String subject = messageTemplate.getSubject();
        String content = messageTemplate.getContent();

        String updatedMessage = content
                .replace("${userName}", user.getName())
                .replace("${resetUrl}", resetUrl)
                .replace("${currentYear}", String.valueOf(Year.now().getValue()));

        sendMail(user.getEmail(), subject, updatedMessage);

        emailLogService.saveLog(buildEmailLog(user.getId(), null, updatedMessage));

    }

    @Override
    public void sendSubscriptionMail(SubscriptionEmailEvent event) throws Exception {

        log.info("sendSubscriptionMail called for {} {}", event.subscription().getId(), event.template());

        Subscription subscription = event.subscription();
        User user = event.user();

        MessageTemplate messageTemplate = getTemplate(event.template());

        String subject = messageTemplate.getSubject();
        String content = messageTemplate.getContent();

        String updatedMessage = buildSubscriptionTemplate(content, subscription, user);

        sendMail(user.getEmail(), subject, updatedMessage);

        emailLogService.saveLog(buildEmailLog(user.getId(), null, updatedMessage));


    }

    private String buildSubscriptionTemplate(String content, Subscription subscription, User user) {

        String amount =  Utils.formatSubscriptionPrice(subscription.getAmount());
        String startDate = Utils.getAbbreviatedDateFormat(subscription.getCurrentSubscriptionStart());
        String endDate = Utils.getAbbreviatedDateFormat(subscription.getCurrentSubscriptionEnd());

        return content
                .replace("${userName}", user.getName())
                .replace("${planName}", "Premium Plan")
                .replace("${amount}", amount)
                .replace("${billingPeriod}", "Monthly")
                .replace("${startDate}", startDate)
                .replace("${nextBillingDate}", endDate)
                .replace("${accessUntilDate}", endDate)
                .replace("${renewalDate}", endDate)
                .replace("${endDate}", endDate)
                .replace("${invoiceId}", subscription.getInvoiceId())
                .replace("${dashboardUrl}", appConfig.getFrontendUrl())
                .replace("${invoiceUrl}", subscription.getInvoiceUrl())
                .replace("${currentYear}", String.valueOf(Year.now().getValue()));

    }

    void sendMail(String to, String subject, String content) throws Exception{

        mailRateLimiter.acquire();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailConfig.getFrom(), mailConfig.getName());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        mailSender.send(message);

    }

    private MessageTemplate getTemplate(String templateCode) {
        return messageTemplateService.getTemplate(AlertChannel.EMAIL, templateCode);
    }

    private EmailLog buildEmailLog(Long userId, Long alertId, String message) {

        return EmailLog.builder()
                .userId(userId)
                .alertId(alertId)
                .content(message)
                .build();
    }
}