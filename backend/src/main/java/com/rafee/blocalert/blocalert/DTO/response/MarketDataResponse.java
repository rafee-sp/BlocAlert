package com.rafee.blocalert.blocalert.DTO.response;

public record MarketDataResponse(
        int totalCoins,
        int totalExchanges,
        long totalMarketCap,
        double marketCapChange24h,
        long volume24h,
        double btcDominance,
        double ethDominance
) {
}
