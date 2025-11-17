package com.rafee.blocalert.blocalert.events.publisher;

import com.rafee.blocalert.blocalert.events.event.AlertNotificationEvent;
import com.rafee.blocalert.blocalert.events.event.CryptoDetailBroadcastEvent;
import com.rafee.blocalert.blocalert.events.event.CryptoTableBroadcastEvent;
import com.rafee.blocalert.blocalert.events.event.MarketStatsBroadcastEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MarketDataPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCryptoTableBroadcast(CryptoTableBroadcastEvent cryptoTableBroadcastEvent) {
        log.info("publishCryptoTableBroadcast called");
        kafkaTemplate.send("crypto-table-data", cryptoTableBroadcastEvent);
    }

    public void publishCryptoDetailBroadcast(CryptoDetailBroadcastEvent cryptoDetailBroadcastEvent) {
        log.info("publishCryptoDetailBroadcast called");
        kafkaTemplate.send("crypto-detail-data", cryptoDetailBroadcastEvent);
    }

    public void publishMarketBroadcast(MarketStatsBroadcastEvent marketStatsBroadcastEvent) {
        log.info("publishMarketBroadcast called");
        kafkaTemplate.send("market-data", marketStatsBroadcastEvent);
    }

    public void publishAlerts(AlertNotificationEvent alertEvent) {
        log.info("publishAlerts called");
        kafkaTemplate.send("crypto-alerts", alertEvent);
    }
}
