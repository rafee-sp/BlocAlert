package com.rafee.blocalert.blocalert.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.events.event.CryptoDetailBroadcastEvent;
import com.rafee.blocalert.blocalert.service.AuthenticationService;
import com.rafee.blocalert.blocalert.service.CryptoService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class CryptoDetailWebSocketHandler extends TextWebSocketHandler {

    private final AuthenticationService authenticationService;
    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<String, String> subscriptionMap = new ConcurrentHashMap<>();

    private static final String AUTH_REQUEST = "AUTH_REQUEST";
    private static final String AUTH_SUCCESS = "AUTH_SUCCESS";
    private static final String CRYPTO_DETAIL_SUB = "SUBSCRIBE_CRYPTO";
    private static final String CRYPTO_DETAIL = "CRYPTO_DATA";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("afterConnectionEstablished called {}", session.getId());
        sessions.add(session);
        session.getAttributes().put("authenticated", false);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Client disconnected : {}", session.getId());
        closeSession(session);
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

            String authResponse = objectMapper.writeValueAsString(Map.of("type", AUTH_SUCCESS));

            sendSessionMessage(session, authResponse);
            return;
        }

        if (!jsonNode.has("type") || !jsonNode.has("cryptoId")) {
            sendErrorMessage(session, "Invalid subscription request");
            return;
        }

        String type = jsonNode.get("type").asText();
        String cryptoId = jsonNode.get("cryptoId").asText();

        if (!type.equals(CRYPTO_DETAIL_SUB)) {
            sendErrorMessage(session, "Invalid subscription request");
            return;
        }

        subscriptionMap.put(session.getId(), cryptoId);

        CryptoMarketData cachedData = cryptoService.getCryptoMarketData(cryptoId);

        sendCryptoDetailMessage(session, cachedData);

    }

    @KafkaListener(topics = "crypto-detail-data", groupId = "blocalert-group")
    public void broadcastCryptoDetailUpdates(CryptoDetailBroadcastEvent cryptoDetailBroadcastEvent) {

        log.info("broadcastCryptoDetailUpdates to {} at {}", sessions.size(), LocalDateTime.now());

        Map<String, CryptoMarketData> cryptoMarketDataMap = cryptoDetailBroadcastEvent.cryptoMarketDataMap();
        Set<WebSocketSession> sessionsToCleanup = new HashSet<>();

        for (WebSocketSession session : sessions) {

            try {

                if (!session.isOpen()) {
                    sessionsToCleanup.add(session);
                    continue;
                }

                String cryptoId = subscriptionMap.get(session.getId());
                if (cryptoId == null) {
                    sessionsToCleanup.add(session);
                    continue;
                }

                CryptoMarketData cachedData = cryptoMarketDataMap.get(cryptoId);

                if (cachedData == null) {
                    log.warn("No crypto data found for {} skipping session {}", cryptoId, session.getId());
                    continue;
                }

                sendCryptoDetailMessage(session, cachedData);

            } catch (Exception e) {
                log.error("Exception occurred at broadcastCryptoDetailUpdates : {}", e.getMessage(), e);
            }
        }

        if (!sessionsToCleanup.isEmpty()) {
            log.info("Cleaning up {} closed session from broadcastCryptoDetailUpdates", sessionsToCleanup.size());
            sessionsToCleanup.forEach(this::closeSession);
        }

    }


    private void sendCryptoDetailMessage(WebSocketSession session, CryptoMarketData cryptoMarketData) throws JsonProcessingException {

        String jsonResponse = objectMapper.writeValueAsString(Map.of("type", CRYPTO_DETAIL, "cryptoData", cryptoMarketData));

        sendSessionMessage(session, jsonResponse);

    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws Exception {

        sendSessionMessage(session, objectMapper.writeValueAsString(Map.of("type", "ERROR", "message", errorMessage)));
    }

    private void sendSessionMessage(WebSocketSession session, String response) {
        try {
            session.sendMessage(new TextMessage(response));
        } catch (Exception e) {
            log.error("Exception occurred while sending data to session : {}", session.getId(), e);
        }
    }

    void closeSession(WebSocketSession session) {
        try {
            subscriptionMap.remove(session.getId());
            sessions.remove(session);
        } catch (Exception e) {
            log.error("Failed to cleanup from missing session removal {}", session.getId(), e);
        }
    }

    @PreDestroy
    public void clearSessions() {
        log.info("Clearing sessions : session {}, subscriptionMap : {}", sessions.size(), subscriptionMap.size());
        sessions.forEach(session -> {
            try {
                session.close(CloseStatus.GOING_AWAY);
                closeSession(session);
            } catch (Exception e) {
                log.error("Error while closing the session {}", session.getId());
            }
        });
    }
}
