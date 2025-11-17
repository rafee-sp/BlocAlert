package com.rafee.blocalert.blocalert.DTO.internal;

import java.math.BigDecimal;

public record CryptoMarketLite(
        String id,
        String symbol,
        String name,
        String image,
        BigDecimal current_price,
        Long market_cap,
        int market_cap_rank,
        long circulating_supply,
        double price_change_percentage_24h

) {
}
