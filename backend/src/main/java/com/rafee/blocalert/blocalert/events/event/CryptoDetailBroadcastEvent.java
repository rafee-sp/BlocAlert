package com.rafee.blocalert.blocalert.events.event;

import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;

import java.util.Map;

public record CryptoDetailBroadcastEvent(Map<String, CryptoMarketData> cryptoMarketDataMap) {
}
