package com.rafee.blocalert.blocalert.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannel;
import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import com.rafee.blocalert.blocalert.DTO.internal.AlertDeliveryResult;
import com.rafee.blocalert.blocalert.DTO.internal.UserAlertNotification;
import com.rafee.blocalert.blocalert.entity.enums.AlertChannelStatus;
import com.rafee.blocalert.blocalert.service.*;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertWebsocketHandler extends TextWebSocketHandler {

    private final AuthenticationService authenticationService;
    private final AlertService alertService;
    private final AlertDeliveryService alertDeliveryService;
    private final ObjectMapper objectMapper;
    private final Map<String, WebSocketSession> sessionsById = new ConcurrentHashMap<>();
    private final Map<String, String> sessionsByUserId = new ConcurrentHashMap<>();

    private static final String AUTH_REQUEST = "AUTH_REQUEST";
    private static final String AUTH_SUCCESS = "AUTH_SUCCESS";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("afterConnectionEstablished called {}", session.getId());

        sessionsById.put(session.getId(), session);
        session.getAttributes().put("authenticated", false);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            log.info("Client disconnected : {}", session.getId());
            String userId = getUserIdFromSession(session);
            sessionsById.remove(session.getId());
            if (StringUtils.hasText(userId)) {
                sessionsByUserId.remove(userId);
            }

        } catch (Exception e) {
            log.warn("Failed to cleanup session {}: {}", session.getId(), e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        log.info("Handle client message : {}", message.getPayload());

        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(message.getPayload());
        } catch (Exception e) {
            sendErrorMessage(session, "Invalid JSON format");
            return;
        }

        if (jsonNode.isMissingNode() || jsonNode.isNull()) {
            sendErrorMessage(session, "Invalid request");
            return;
        }

        if (Boolean.FALSE.equals(session.getAttributes().get("authenticated"))) {

            if (!jsonNode.has("type") || !AUTH_REQUEST.equals(jsonNode.get("type").asText())) {
                log.info("");
                sendErrorMessage(session, "Unauthorized");
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
                return;
            }

            String token = jsonNode.has("token") ? jsonNode.get("token").asText() : null;

            Long userId = authenticationService.validateAndGetUserId(token);
            if (userId == null || userId <= 0) {
                sendErrorMessage(session, "Invalid token");
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
                return;
            }

            session.getAttributes().put("authenticated", true);
            session.getAttributes().put("userId", userId);
            sessionsByUserId.put(String.valueOf(userId), session.getId());

            String authResponse = objectMapper.writeValueAsString(Map.of("type", AUTH_SUCCESS));

            sendSessionMessage(session, authResponse);
            return;
        }

        sendErrorMessage(session, "Invalid subscription request");

    }

    @KafkaListener(topics = "websocket-alerts", groupId = "blocalert-group")
    public void broadcastAlerts(AlertNotificationEvent alertNotificationEvent) {

        log.info("broadcastAlerts to {} at {}", sessionsById.size(), LocalDateTime.now());

        try {

            List<UserAlertNotification> cachedAlerts = alertNotificationEvent.alertList();

            // Group alerts by userId
            Map<Long, List<UserAlertNotification>> alertsByUser = cachedAlerts.stream()
                    .collect(Collectors.groupingBy(UserAlertNotification::getUserId));

            List<AlertDeliveryResult> deliveryResults = new ArrayList<>();

            // Send once per user
            alertsByUser.forEach((userId, userAlerts) -> {

                try {
                    String userIdStr = userId.toString();
                    String sessionId = sessionsByUserId.get(userIdStr);

                    log.info("userId: {}, alerts count: {}", userIdStr, userAlerts.size());
                    log.info("sessionId: {}", sessionId);

                    if (sessionId != null) {
                        WebSocketSession session = sessionsById.get(sessionId);

                        if (session == null) {
                            log.error("session is null for userId {}", userId);
                            removeSession(userIdStr, sessionId);
                            handleDeliveryStatus(deliveryResults, userAlerts, false);
                            return;
                        }

                        if (Boolean.FALSE.equals(session.getAttributes().get("authenticated"))) {
                            log.error("session is unauthenticated for userId {}", userId);
                            removeSession(userIdStr, sessionId);
                            handleDeliveryStatus(deliveryResults, userAlerts, false);
                            return;
                        }

                        if (session.isOpen()) {
                            log.info("sending {} alerts for session: {}", userAlerts.size(), sessionId);
                            try {
                                String jsonResponse = objectMapper.writeValueAsString(
                                        Map.of("type", "ALERTS", "alertData", userAlerts)
                                );
                                sendSessionMessage(session, jsonResponse);
                                handleDeliveryStatus(deliveryResults, userAlerts, true);
                            } catch (JsonProcessingException e) {
                                handleDeliveryStatus(deliveryResults, userAlerts, false);
                                log.error("Error processing json", e);
                            }
                        } else {
                            log.error("session is not open for userId {}", userId);
                            removeSession(userIdStr, sessionId);
                            handleDeliveryStatus(deliveryResults, userAlerts, false);
                        }
                    } else {
                        log.error("No session found for userId {}", userId);
                        handleDeliveryStatus(deliveryResults, userAlerts, false);
                    }

                } catch (Exception e) {
                    log.error("Error processing alert broadcast for user {}", userId, e);
                }

            });

            try {
                alertService.setAlertAsTriggered(deliveryResults);
                alertService.cleanupRedisAlerts(cachedAlerts);
                alertDeliveryService.recordAlertDeliveries(AlertChannel.WEBSOCKET, deliveryResults);
            } catch (Exception e) {
                log.info("Error while recording websocket deliveries");
            }

        } catch (Exception e) {
            log.error("Error occurred in broadcastAlerts ", e);
        }
    }

    private void handleDeliveryStatus(List<AlertDeliveryResult> deliveryResults, List<UserAlertNotification> userAlerts, boolean isSuccess) {

        userAlerts.forEach(alert -> deliveryResults.add(new AlertDeliveryResult(alert.getAlertId(), isSuccess ? AlertChannelStatus.DELIVERED : AlertChannelStatus.FAILED, LocalDateTime.now())));

    }

    private void removeSession(String userId, String sessionId) {
        try {
            sessionsByUserId.remove(userId);
            sessionsById.remove(sessionId);
        } catch (Exception e) {
            log.error("Error removing the sessions map data : sessionId {}, userId {}", sessionId, userId);
        }
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage){
        try {
            sendSessionMessage(session, objectMapper.writeValueAsString(Map.of("type", "ERROR", "message", errorMessage)));
        } catch (Exception e) {
            log.error("Exception occurred while sending sendErrorMessage : {}", session.getId(), e);
        }
    }


    private void sendSessionMessage(WebSocketSession session, String response) {
        try {
            synchronized (session) {
                session.sendMessage(new TextMessage(response));
            }
        } catch (Exception e) {
            log.error("Exception occurred while sending the crypto alert to session : {}", session.getId(), e);
        }
    }

    private String getUserIdFromSession(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? userId.toString() : null;
    }

    @PreDestroy
    public void clearSessions() {
        log.info("Clearing sessions : session {}, userSession : {}", sessionsById.size(), sessionsByUserId.size());
        sessionsById.values().forEach(session -> {
            try {
                session.close(CloseStatus.GOING_AWAY);
            } catch (Exception e) {
                log.error("Error while closing the session {}", session.getId());
            }
        });
        sessionsById.clear();
        sessionsByUserId.clear();
    }

}
