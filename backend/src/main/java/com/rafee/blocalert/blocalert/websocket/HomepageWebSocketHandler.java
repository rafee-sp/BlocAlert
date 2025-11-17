package com.rafee.blocalert.blocalert.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.DTO.PageDTO;
import com.rafee.blocalert.blocalert.DTO.Pagination;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;
import com.rafee.blocalert.blocalert.DTO.internal.MarketStatsData;
import com.rafee.blocalert.blocalert.DTO.response.CryptoResponse;
import com.rafee.blocalert.blocalert.events.event.CryptoTableBroadcastEvent;
import com.rafee.blocalert.blocalert.events.event.MarketStatsBroadcastEvent;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class HomepageWebSocketHandler extends TextWebSocketHandler {

    private final CryptoService cryptoService;
    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final Map<String, PageDTO> subscriptionMap = new ConcurrentHashMap<>();

    private static final String TYPE_SUBSCRIBE_PAGE = "SUBSCRIBE_CRYPTO_PAGE";
    private static final String TYPE_SUBSCRIBE_MARKET_DATA = "SUBSCRIBE_MARKET_DATA";
    private static final String TYPE_CRYPTO_DATA = "CRYPTO_DATA";
    private static final String TYPE_MARKET_DATA = "MARKET_DATA";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        log.info("Client connected : {}", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
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

        String type = jsonNode.path("type").asText(null);

        if (type == null) {
            sendErrorMessage(session, "Invalid subscription type");
            return;
        }

        switch (type) {

            case TYPE_SUBSCRIBE_PAGE -> handleCryptoSubscription(session, jsonNode);
            case TYPE_SUBSCRIBE_MARKET_DATA -> handleMarketDataSubscription(session);
            default -> sendErrorMessage(session, "Invalid subscription type");
        }
    }

    private void handleCryptoSubscription(WebSocketSession session, JsonNode jsonNode) throws Exception {

        int page = jsonNode.path("page").asInt(-1);
        int size = jsonNode.path("size").asInt(-1);

        if (!isValidPageRequest(page, size)) {
            sendErrorMessage(session, "Invalid page or size");
            return;
        }

        List<CryptoMarketLite> cryptoMarketLiteList = cryptoService.getCachedCryptoData();

        int totalPages = (int) Math.ceil((double) cryptoMarketLiteList.size() / size);
        if (page > totalPages) page = totalPages;

        PageDTO pageDTO = new PageDTO(page, size);
        subscriptionMap.put(session.getId(), pageDTO);

        sendCryptoDataMessage(session, page, size, cryptoMarketLiteList);
    }

    private void handleMarketDataSubscription(WebSocketSession session) throws IOException {

        MarketStatsData marketStatsData = cryptoService.getCachedMarketStats();
        sendMarketStatsMessage(session, marketStatsData);
    }

    @KafkaListener(topics = "crypto-table-data", groupId = "blocalert-group")
    public void broadcastCryptoUpdates(CryptoTableBroadcastEvent cryptoTableBroadcastEvent) {

        log.info("broadcastCryptoUpdates to {} sessions at {}", sessions.size(), LocalDateTime.now());

        List<CryptoMarketLite> cryptoMarketLiteList = cryptoTableBroadcastEvent.cryptoMarketLiteList();
        Set<WebSocketSession> sessionsToCleanup = new HashSet<>();

        for (WebSocketSession session : sessions) {

            try {

                if (!session.isOpen()) {
                    sessionsToCleanup.add(session);
                    continue;
                }

                PageDTO pageDTO = subscriptionMap.get(session.getId());
                if (pageDTO == null) {
                    sessionsToCleanup.add(session);
                    continue;
                }

                sendCryptoDataMessage(session, pageDTO.page(), pageDTO.size(), cryptoMarketLiteList);

            } catch (Exception e) {
                log.error("Exception occurred at broadcastCryptoUpdates : {}", e.getMessage(), e);
            }
        }

        if (!sessionsToCleanup.isEmpty()) {
            log.info("Cleaning up {} closed session from broadcastCryptoUpdates ", sessionsToCleanup.size());
            sessionsToCleanup.forEach(this::closeSession);
        }
    }

    @KafkaListener(topics = "market-data", groupId = "blocalert-group")
    public void broadcastMarketStatsUpdates(MarketStatsBroadcastEvent marketStatsBroadcastEvent) {

        log.info("broadcastMarketStatsUpdates to {} at {}", sessions.size(), LocalDateTime.now());

        MarketStatsData marketStatsData = marketStatsBroadcastEvent.marketStatsData();
        Set<WebSocketSession> sessionsToCleanup = new HashSet<>();

        for (WebSocketSession session : sessions) {
            try {

                if (!session.isOpen()) {
                    sessionsToCleanup.add(session);
                    continue;
                }

                sendMarketStatsMessage(session, marketStatsData);

            } catch (Exception e) {
                log.error("Exception occurred at broadcastMarketStatsUpdate : {}", e.getMessage(), e);
            }
        }

        if (!sessionsToCleanup.isEmpty()) {
            log.info("Cleaning up {} closed session from broadcastMarketStatsUpdates", sessionsToCleanup.size());
            sessionsToCleanup.forEach(this::closeSession);
        }
    }

    public void sendCryptoDataMessage(WebSocketSession session, int page, int size, List<CryptoMarketLite> cryptoMarketLiteList) throws JsonProcessingException {

        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, cryptoMarketLiteList.size());
        List<CryptoMarketLite> paginatedCryptoData = cryptoMarketLiteList.subList(fromIndex, toIndex);

        int totalPages = (int) Math.ceil((double) cryptoMarketLiteList.size() / size);
        Pagination pagination = new Pagination(page, size, totalPages);

        CryptoResponse response = new CryptoResponse(paginatedCryptoData, pagination);

        String jsonResponse = objectMapper.writeValueAsString(Map.of("type", TYPE_CRYPTO_DATA, "data", response));

        sendSessionMessage(session, jsonResponse);

    }

    private void sendMarketStatsMessage(WebSocketSession session, MarketStatsData marketData) throws IOException {

        String jsonResponse = objectMapper.writeValueAsString(Map.of("type", TYPE_MARKET_DATA, "data", marketData));

        sendSessionMessage(session, jsonResponse);
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws Exception {

        sendSessionMessage(session, objectMapper.writeValueAsString(Map.of("type", "ERROR", "message", errorMessage)));
    }


    private void sendSessionMessage(WebSocketSession session, String response) {
        try {
            session.sendMessage(new TextMessage(response));
        } catch (Exception e) {
            log.error("Exception occurred while sending the crypto data to session : {}", session.getId(), e);
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

    private boolean isValidPageRequest(int page, int size) {
        return page > 0 && size > 0 && size <= 100;
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
