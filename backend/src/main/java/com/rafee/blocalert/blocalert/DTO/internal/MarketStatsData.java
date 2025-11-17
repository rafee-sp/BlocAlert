package com.rafee.blocalert.blocalert.DTO.internal;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarketStatsData {

    private int totalCoins;
    private int totalExchanges;
    private long totalMarketCap;
    private double marketCapChange24h;
    private long volume24h;
    private double btcDominance;
    private double ethDominance;

    public static MarketStatsData from(JsonNode marketData) {

        return new MarketStatsData(
                marketData.path("active_cryptocurrencies").asInt(0),
                marketData.path("markets").asInt(0),
                marketData.path("total_market_cap").path("usd").asLong(0L),
                marketData.path("market_cap_change_percentage_24h_usd").asDouble(0.0),
                marketData.path("total_volume").path("usd").asLong(0L),
                marketData.path("market_cap_percentage").path("btc").asDouble(0.0),
                marketData.path("market_cap_percentage").path("eth").asDouble(0.0)
        );
    }
}
