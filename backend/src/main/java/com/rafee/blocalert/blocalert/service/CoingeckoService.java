package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.DTO.internal.MarketStatsData;

import java.util.List;
import java.util.Map;

public interface CoingeckoService {

    List<CryptoMarketData> fetchCryptoMarketData();

    MarketStatsData fetchMarketStats();

    Map<String, Object> getCryptoChartData(String cryptoId,String timeframe);
}
