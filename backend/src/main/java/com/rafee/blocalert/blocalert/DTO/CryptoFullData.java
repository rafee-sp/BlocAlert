package com.rafee.blocalert.blocalert.DTO;

import java.time.Instant;
import java.time.LocalDateTime;

public record CryptoFullData (

    String id,
    String symbol,
    String name,
    String image,
    double current_price,
    Long market_cap,
    int market_cap_rank,
    long circulating_supply,
    double price_change_percentage_24h,
    long fully_diluted_valuation,
    long total_supply,
    double high_24h,
    double low_24h,
    double market_cap_change_percentage_24h,
    long max_supply,
    double ath,
    double atl,
    String atl_date,
    String ath_date
){}
