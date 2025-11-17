package com.rafee.blocalert.blocalert.service;

import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.DTO.internal.MarketStatsData;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CryptoService {

    void fetchAndCacheCryptoData();

    void fetchAndCacheMarketStats();

    List<CryptoMarketLite> getCachedCryptoData();

    MarketStatsData getCachedMarketStats();

    CryptoMarketData getCryptoMarketData(String cryptoId);

    List<CryptoMarketLite> searchCrypto(String searchTerm);

    Map<String, Object> getChartData(String id, String timeframe);

    CryptoMarketData getCryptoData(String cryptoId);

    Map<String, CryptoMarketData> getMultipleCryptoData(Set<String> cryptoIds);

    Map<String, BigDecimal> getCryptoPrice(String cryptoId);

}
