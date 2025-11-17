package com.rafee.blocalert.blocalert.config;

import com.rafee.blocalert.blocalert.websocket.AlertWebsocketHandler;
import com.rafee.blocalert.blocalert.websocket.CryptoDetailWebSocketHandler;
import com.rafee.blocalert.blocalert.websocket.HomepageWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final HomepageWebSocketHandler homepageWebSocketHandler;
    private final CryptoDetailWebSocketHandler cryptoDetailWebSocketHandler;
    private final AlertWebsocketHandler alertWebsocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("websocket configured");
        registry.addHandler(homepageWebSocketHandler, "/ws/homepage")
                .setAllowedOrigins("*");

        registry.addHandler(cryptoDetailWebSocketHandler, "/ws/crypto")
                .setAllowedOrigins("*");

        registry.addHandler(alertWebsocketHandler, "/ws/alerts")
                .setAllowedOrigins("*");
    }

}

