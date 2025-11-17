package com.rafee.blocalert.blocalert.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.DTO.response.SubscriptionDetailResponse;
import com.rafee.blocalert.blocalert.DTO.response.SubscriptionResponse;
import com.rafee.blocalert.blocalert.config.StripeConfig;
import com.rafee.blocalert.blocalert.entity.Subscription;
import com.rafee.blocalert.blocalert.entity.User;
import com.rafee.blocalert.blocalert.entity.enums.SubscriptionStatus;
import com.rafee.blocalert.blocalert.events.event.SubscriptionEmailEvent;
import com.rafee.blocalert.blocalert.exception.ResourceNotFoundException;
import com.rafee.blocalert.blocalert.repository.SubscriptionRepository;
import com.rafee.blocalert.blocalert.service.SubscriptionService;
import com.rafee.blocalert.blocalert.service.UserService;
import com.rafee.blocalert.blocalert.service.WebhookEventService;
import com.rafee.blocalert.blocalert.utils.Utils;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static com.rafee.blocalert.blocalert.utils.JsonUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final UserService userService;
    private final SubscriptionRepository subscriptionRepository;
    private final StripeConfig stripeConfig;
    private final ObjectMapper objectMapper;
    private final WebhookEventService webhookEventService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public String createSubscription(Long userId) throws StripeException {

        log.info("createSubscription called for {}", userId);

        User user = userService.getUser(userId);

        String stripeCustomerId = getOrCreateCustomerId(user);

        log.debug("stripeCustomerId : {}", stripeCustomerId);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomer(stripeCustomerId)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(stripeConfig.getPriceId())
                                .setQuantity(1L)
                                .build()
                )
                .setSuccessUrl(stripeConfig.getSuccessUrl())
                .setCancelUrl(stripeConfig.getCancelUrl())
                .setExpiresAt(Instant.now().plus(30L, ChronoUnit.MINUTES).getEpochSecond())
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .putMetadata("userId", user.getId().toString())
                .putMetadata("auth0Id", user.getAuth0Id())
                .build();

        Session session = Session.create(params);

        createSubscriptionRecord(user, session.getId());

        log.debug("sessionUrl : {}", session.getUrl());

        return session.getUrl();

    }

    @Override
    public SubscriptionResponse getSubscriptionSessionDetails(Long userId, String sessionId) throws StripeException {

        log.info("getSubscriptionSessionDetails called for {} - {}", userId, sessionId);

        Session session = Session.retrieve(sessionId);

        User user = userService.getUser(userId);

        if (session == null || session.getSubscription() == null) {
            log.error("No subscription found");
            throw new ResourceNotFoundException("Session not found for Id " + sessionId);
        }

        String paymentStatus = session.getPaymentStatus();

        log.debug("paymentStatus : {}", paymentStatus);

        if (!"paid".equals(paymentStatus)) {
            return SubscriptionResponse.builder().isPaymentCheckedOut(false)
                    .isSessionValid(true)
                    .isSubscriptionSuccess(false)
                    .build();
        }

        Customer customer = Customer.retrieve(session.getCustomer());

        if (customer == null || !customer.getId().equals(user.getStripeCustomerId())) {
            log.debug("No customer found");
            return SubscriptionResponse.builder().isPaymentCheckedOut(true)
                    .isSessionValid(false)
                    .isSubscriptionSuccess(false)
                    .build();
        }

        if (!user.isSubscribed()) {

            log.debug("User's subscription is waiting to be activated {}", userId);

            return SubscriptionResponse.builder().isPaymentCheckedOut(true)
                    .isSessionValid(true)
                    .isSubscriptionSuccess(false)
                    .build();
        }

        return buildSubscriptionResponse(user, sessionId);
    }

    @Override
    @Transactional
    public void cancelSubscription(Long userId) throws StripeException {

        log.info("cancelSubscription called for {}", userId);

        User user = userService.getUser(userId);

        Subscription subscription = subscriptionRepository.findByUser_IdAndSubscriptionStatus(user.getId(), SubscriptionStatus.ACTIVE).orElseThrow(() -> new ResourceNotFoundException("Not active Subscription found for user "+ userId));

        com.stripe.model.Subscription stripeSub = com.stripe.model.Subscription.retrieve(subscription.getSubscriptionId());

        SubscriptionUpdateParams updateParams = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();
        stripeSub.update(updateParams);

        subscription.setCanceledAt(LocalDateTime.now());
        subscription.setIsCancelled(true);
        subscription.setSubscriptionStatus(SubscriptionStatus.CANCELLING);
        subscriptionRepository.save(subscription);

        eventPublisher.publishEvent(new SubscriptionEmailEvent(subscription, user, "SUBSCRIPTION_CANCEL"));

        log.info("Subscription set to cancelled");

    }

    @Override
    public SubscriptionDetailResponse getUserSubscriptionDetails(Long userId) {

        log.info("getUserSubscriptionDetails called for {}", userId);

        return subscriptionRepository.getRecentUserSubscription(userId)
                .orElseGet(() -> new SubscriptionDetailResponse(null, SubscriptionStatus.INACTIVE, null));
    }

    @Override
    @Transactional
    public void handleStripeCallback(Event event) throws JsonProcessingException {

        log.info("handleStripeCallback called for event : {}", event.getType());

        switch (event.getType()) {
            case "checkout.session.completed" -> handleCheckoutCompleted(event);
            case "invoice.paid" -> handleInvoicePaid(event);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed(event);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted(event);
            case "invoice.upcoming" -> handleInvoiceUpcoming(event);
            default -> log.warn("Unhandled event type : {}", event.getType());
        }
    }

    private void handleCheckoutCompleted(Event event) throws JsonProcessingException {

        log.info("handleCheckoutCompleted called");

        if (webhookEventService.existsByEventId(event.getId())) {
            log.warn("handleCheckoutCompleted - Stripe sends duplicate event  : {}", event.getId());
            return;
        }

        JsonNode dataObject = getDataObject(event);

        String sessionId = getStringField(dataObject, "id");

        log.debug("sessionId : {}", sessionId);

        JsonNode metadata = getJsonNode(dataObject, "metadata");
        String userId = getStringField(metadata, "userId");

        log.debug("userId : {}", userId);

        String subscriptionId = getStringField(dataObject, "subscription");

        Subscription subscription = subscriptionRepository.findByUser_IdAndSessionId(Long.parseLong(userId), sessionId).orElseThrow(() -> new ResourceNotFoundException("subscription not found"));

        subscription.setSubscriptionId(subscriptionId);
        subscription.setSubscriptionStatus(SubscriptionStatus.PROCESSING);

        subscriptionRepository.save(subscription);

        webhookEventService.recordWebhookEvent(event.getId(), event.getType());

    }

    private void handleInvoicePaid(Event event) throws JsonProcessingException {

        // TODO : Stripe sends invoice.paid before checkout.session.completed

        log.info("handleInvoicePaid called");

        if (webhookEventService.existsByEventId(event.getId())) {
            log.warn("handleInvoicePaid - Stripe sends duplicate event : {}", event.getId());
            return;
        }

        JsonNode dataObject = getDataObject(event);

        String customerId = getStringField(dataObject, "customer");

        String invoiceId = getStringField(dataObject, "id");

        String subscriptionId = getSubscriptionId(dataObject);

        int amount = getJsonNode(dataObject, "amount_due").asInt();

        String billingReason = getStringField(dataObject, "billing_reason");

        String invoicePdfUrl = getStringField(dataObject, "invoice_pdf");

        JsonNode lines = getJsonNode(dataObject, "lines");
        JsonNode lineData = getJsonNode(lines, "data");

        if (!lineData.isArray() || lineData.isEmpty()) {
            throw new IllegalStateException("Invoice lines data is missing or empty");
        }

        JsonNode lineItem = lineData.get(0);
        JsonNode period = getJsonNode(lineItem, "period");

        long periodStart = getJsonNode(period, "start").asLong();
        long periodEnd = getJsonNode(period, "end").asLong();

        User user = userService.getUserByStripeCustomerId(customerId);

        Subscription subscription = getSubscription(user.getId(), subscriptionId);

        String emailTemplate;

        if (billingReason.equals("subscription_create")) {

            subscription.setActivatedAt(LocalDateTime.now());
            subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            emailTemplate = "SUBSCRIPTION_SUCCESS";

        } else if (billingReason.equals("subscription_cycle")) {

            subscription.setLastRenewedAt(LocalDateTime.now());
            subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
            emailTemplate = "SUBSCRIPTION_RENEWAL";

        } else {
            log.warn("handleInvoicePaid - Unhandled billing reason : {}", billingReason);
            return;
        }

        subscription.setInvoiceId(invoiceId);
        subscription.setInvoiceUrl(invoicePdfUrl);  // TODO : Create table to manage historical data
        subscription.setAmount(amount);
        subscription.setCurrentSubscriptionStart(getDateTimeFromUtc(periodStart));
        subscription.setCurrentSubscriptionEnd(getDateTimeFromUtc(periodEnd));
        subscriptionRepository.save(subscription);

        if (billingReason.equals("subscription_create")) {
            userService.upgradeToPremium(user.getId());
        }

        eventPublisher.publishEvent(new SubscriptionEmailEvent(subscription, user, emailTemplate));

        webhookEventService.recordWebhookEvent(event.getId(), event.getType());
    }

    private void handleInvoicePaymentFailed(Event event) throws JsonProcessingException {

        log.info("handleInvoicePaymentFailedEvent called");

        if (webhookEventService.existsByEventId(event.getId())) {
            log.warn("handleInvoicePaymentFailed - Stripe sends duplicate event : {}", event.getId());
            return;
        }

        JsonNode dataObject = getDataObject(event);

        String customerId = getStringField(dataObject, "customer");

        String subscriptionId = getSubscriptionId(dataObject);

        String billingReason = getStringField(dataObject, "billing_reason");

        User user = userService.getUserByStripeCustomerId(customerId);

        Subscription subscription = getSubscription(user.getId(), subscriptionId);

        if (!billingReason.equals("subscription_cycle")) {
            log.warn("handleInvoicePaymentFailed - Unhandled billing reason {}", billingReason);
            return;
        }

        log.debug("customerId {}, subscriptionId : {}, billingReason : {}", customerId, subscriptionId, billingReason);

        markSubscriptionPastDue(subscription);

        userService.downgradeUserToFree(user.getId());

        // TODO : send payment failed email

        webhookEventService.recordWebhookEvent(event.getId(), event.getType());

    }

    private void handleInvoiceUpcoming(Event event) throws JsonProcessingException {

        log.info("handleInvoiceUpcoming called");

        if (webhookEventService.existsByEventId(event.getId())) {
            log.warn("handleInvoiceUpcoming - Stripe sends duplicate event : {}", event.getId());
            return;
        }

        JsonNode dataObject = getDataObject(event);

        String customerId = getStringField(dataObject, "customer");

        String subscriptionId = getSubscriptionId(dataObject);

        int amount = getJsonNode(dataObject, "amount_due").asInt();

        String billingReason = getStringField(dataObject, "billing_reason");

        JsonNode lines = getJsonNode(dataObject, "lines");
        JsonNode lineData = getJsonNode(lines, "data");

        if (!lineData.isArray() || lineData.isEmpty()) {
            throw new IllegalStateException("Invoice lines data is missing or empty");
        }

        JsonNode lineItem = lineData.get(0);
        JsonNode period = getJsonNode(lineItem, "period");

        long periodStart = getJsonNode(period, "start").asLong();
        long periodEnd = getJsonNode(period, "end").asLong();

        User user = userService.getUserByStripeCustomerId(customerId);

        Subscription subscription = getSubscription(user.getId(), subscriptionId);

        if (!subscriptionRepository.existsByUser_IdAndSubscriptionId(user.getId(), subscriptionId)) {
            throw new ResourceNotFoundException("Subscription not exists for user " + user.getId());
        }

        if (!billingReason.equals("upcoming")) {
            log.warn("handleInvoiceUpcoming - Unhandled billing reason {}", billingReason);
            return;
        }

        log.debug("customerId {}, subscriptionId : {}, billingReason : {}, amount : {}, periodStart : {}, periodEnd : {}", customerId, subscriptionId, billingReason, amount, periodStart, periodEnd);
        eventPublisher.publishEvent(new SubscriptionEmailEvent(subscription, user, "SUBSCRIPTION_RENEWAL_REMINDER"));

        webhookEventService.recordWebhookEvent(event.getId(), event.getType());

    }

    private void handleSubscriptionDeleted(Event event) throws JsonProcessingException {

        log.info("handleSubscriptionDeleted called");

        if (webhookEventService.existsByEventId(event.getId())) {
            log.warn("handleSubscriptionDeleted - Stripe sends duplicate event : {}", event.getId());
            return;
        }

        JsonNode dataObject = getDataObject(event);

        String subscriptionId = getStringField(dataObject, "id");
        String customerId = getStringField(dataObject, "customer");

        User user = userService.getUserByStripeCustomerId(customerId);

        Subscription subscription = getSubscription(user.getId(), subscriptionId);

        log.debug("customerId {}, subscriptionId : {}", customerId, subscriptionId);

        expireOrCancelSubscription(subscription);

        userService.downgradeUserToFree(user.getId());

        if (!subscription.getIsCancelled()) {
            eventPublisher.publishEvent(new SubscriptionEmailEvent(subscription, user, "SUBSCRIPTION_END"));
        }

        // TODO : send different email for already cancelled subscription

        webhookEventService.recordWebhookEvent(event.getId(), event.getType());

    }

    private String getOrCreateCustomerId(User user) throws StripeException {

        if (StringUtils.hasText(user.getStripeCustomerId())) return user.getStripeCustomerId();

        CustomerCreateParams params = CustomerCreateParams.builder()
                .setName(user.getName())
                .setEmail(user.getEmail())
                .putMetadata("userId", user.getId().toString())
                .putMetadata("auth0Id", user.getAuth0Id())
                .build();

        Customer customer = Customer.create(params);

        String customerId = customer.getId();

        userService.updateCustomerId(user.getId(), customerId);

        return customerId;

    }

    private Subscription getSubscription(Long userId, String subscriptionId) {
        return subscriptionRepository.findByUser_IdAndSubscriptionId(userId, subscriptionId).orElseThrow(() -> new ResourceNotFoundException("Subscription not found with subscription Id  " + subscriptionId));
    }

    private void createSubscriptionRecord(User user, String sessionId) {

        Subscription subscription = Subscription.builder()
                .sessionId(sessionId)
                .user(user)
                .subscriptionStatus(SubscriptionStatus.PENDING)
                .build();

        subscriptionRepository.save(subscription);

    }

    private void markSubscriptionPastDue(Subscription subscription) {

        subscription.setSubscriptionStatus(SubscriptionStatus.PAST_DUE);
        subscriptionRepository.save(subscription);
    }

    private void expireOrCancelSubscription(Subscription subscription) {

        if (subscription.getIsCancelled()) {
            subscription.setSubscriptionStatus(SubscriptionStatus.CANCELLED);
        } else {
            subscription.setSubscriptionStatus(SubscriptionStatus.EXPIRED);
            subscription.setExpiredAt(LocalDateTime.now());
        }
        subscriptionRepository.save(subscription);
    }

    private SubscriptionResponse buildSubscriptionResponse(User user, String sessionId) {

        Subscription subscription = subscriptionRepository.findByUser_IdAndSessionId(user.getId(), sessionId).orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        double amount = subscription.getAmount() / 100.00;
        LocalDateTime subscriptionStart = subscription.getCurrentSubscriptionStart();
        LocalDateTime subscriptionEnd = subscription.getCurrentSubscriptionEnd();
        String maskedUserEmail = Utils.maskEmail(user.getEmail());

        return SubscriptionResponse.builder().isPaymentCheckedOut(true)
                .isSessionValid(true)
                .isSubscriptionSuccess(true)
                .amount(amount)
                .subscriptionStart(subscriptionStart.toLocalDate())
                .subscriptionEnd(subscriptionEnd.toLocalDate())
                .customerEmail(maskedUserEmail)
                .build();
    }

    private JsonNode getDataObject(Event event) throws JsonProcessingException {

        JsonNode eventNode = objectMapper.readTree(event.toJson());

        JsonNode dataObject = eventNode.path("data").path("object");

        if (dataObject.isMissingNode() || dataObject.isNull()) {
            throw new IllegalStateException("dataObject is missing in event");
        }

        return  dataObject;
    }

    private String getSubscriptionId(JsonNode dataObject) {

        if (dataObject.has("subscription") && !dataObject.get("subscription").isNull()) {
            return getStringField(dataObject, "subscription");
        }

        JsonNode parentNode = dataObject.get("parent");
        if (parentNode != null && parentNode.has("subscription_details")) {
            JsonNode subDetails = parentNode.get("subscription_details");
            if (subDetails.has("subscription") && !subDetails.get("subscription").isNull()) {
                return getStringField(subDetails, "subscription");
            }
        }

        throw new IllegalStateException("Subscription ID not found in invoice.paid event");
    }

    private LocalDateTime getDateTimeFromUtc(Long dateTime) {
        return  LocalDateTime.ofInstant(
                Instant.ofEpochSecond(dateTime),
                ZoneId.systemDefault());
    }

}
