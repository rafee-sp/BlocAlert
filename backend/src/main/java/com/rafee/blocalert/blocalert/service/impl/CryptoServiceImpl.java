package com.rafee.blocalert.blocalert.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.DTO.internal.MarketStatsData;
import com.rafee.blocalert.blocalert.events.event.SendAlertEvent;
import com.rafee.blocalert.blocalert.events.publisher.AlertNotificationPublisher;
import com.rafee.blocalert.blocalert.exception.ResourceNotFoundException;
import com.rafee.blocalert.blocalert.events.event.CryptoTableBroadcastEvent;
import com.rafee.blocalert.blocalert.events.event.CryptoDetailBroadcastEvent;
import com.rafee.blocalert.blocalert.events.event.MarketStatsBroadcastEvent;
import com.rafee.blocalert.blocalert.events.publisher.MarketDataPublisher;
import com.rafee.blocalert.blocalert.service.CoingeckoService;
import com.rafee.blocalert.blocalert.service.CryptoService;
import com.rafee.blocalert.blocalert.service.RedisService;
import com.rafee.blocalert.blocalert.utils.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CryptoServiceImpl implements CryptoService {

    private final ObjectMapper objectMapper;
    private final RedisService redisService;
    private final CoingeckoService coingeckoService;
    private final MarketDataPublisher marketDataPublisher;
    private final AlertNotificationPublisher alertNotificationPublisher;

    @Override
    public void fetchAndCacheCryptoData() {

        log.info("fetchAndCacheCryptoData called");

        List<CryptoMarketData> fullList = coingeckoService.fetchCryptoMarketData();
        List<CryptoMarketLite> liteList = mapToLiteData(fullList);

        redisService.hashPutAll(RedisKeys.CRYPTO_FULL_HASH, serializeHashData(fullList));
        redisService.valueSet(RedisKeys.CRYPTO_DATA_LITE, liteList);

        marketDataPublisher.publishCryptoTableBroadcast(new CryptoTableBroadcastEvent(liteList));
        marketDataPublisher.publishCryptoDetailBroadcast(buildCryptoBroadcastEvent(fullList));
        alertNotificationPublisher.publishAlertNotifications(new SendAlertEvent(liteList));

        log.info("Cached {} cryptos and published events", liteList.size());
    }

    private CryptoDetailBroadcastEvent buildCryptoBroadcastEvent(List<CryptoMarketData> fullList) {

        Map<String, CryptoMarketData> cryptoMarketDataMap = fullList
                .stream()
                .collect(Collectors.toMap(CryptoMarketData::id, Function.identity()));

        return new CryptoDetailBroadcastEvent(cryptoMarketDataMap);
    }

    @Override
    public void fetchAndCacheMarketStats() {

        log.info("fetchAndCacheMarketStats called");

        MarketStatsData marketStats = coingeckoService.fetchMarketStats();
        redisService.valueSet(RedisKeys.MARKET_STATS, marketStats);

        marketDataPublisher.publishMarketBroadcast(new MarketStatsBroadcastEvent(marketStats));

        log.info("Cached market stats and published events");
    }

    @Override
    public List<CryptoMarketLite> getCachedCryptoData() {

        log.info("getCachedCryptoData called");

        List<CryptoMarketLite> cachedCryptoList = redisService.valueGet(RedisKeys.CRYPTO_DATA_LITE, new TypeReference<List<CryptoMarketLite>>() {
        });

        if (cachedCryptoList != null) {
            return cachedCryptoList;
        }

        log.warn("CryptoMarketLite is null in redis - refetching");
        fetchAndCacheCryptoData();
        cachedCryptoList = redisService.valueGet(RedisKeys.CRYPTO_DATA_LITE, new TypeReference<List<CryptoMarketLite>>() {
        });

        if (cachedCryptoList == null) {
            throw new ResourceNotFoundException("CryptoMarketLite not found");
        }

        return cachedCryptoList;
    }

    @Override
    public MarketStatsData getCachedMarketStats() {

        MarketStatsData cachedMarketStats = redisService.valueGet(RedisKeys.MARKET_STATS, new TypeReference<MarketStatsData>() {
        });

        if (cachedMarketStats != null) {
            return cachedMarketStats;
        }

        log.warn("MarketStatsData is null in redis - refetching");
        fetchAndCacheMarketStats();
        cachedMarketStats = redisService.valueGet(RedisKeys.MARKET_STATS, new TypeReference<MarketStatsData>() {
        });

        if (cachedMarketStats == null) {
            throw new ResourceNotFoundException("MarketStatsData not found");
        }

        return cachedMarketStats;
    }

    @Override
    public CryptoMarketData getCryptoMarketData(String cryptoId) {

        return redisService.hashGet(RedisKeys.CRYPTO_FULL_HASH, cryptoId, CryptoMarketData.class);

    }

    @Override
    public List<CryptoMarketLite> searchCrypto(String searchTermRaw) {

        log.info("searchCrypto called with term: {}", searchTermRaw);

        if (searchTermRaw == null || searchTermRaw.isBlank()) {
            return Collections.emptyList();
        }

        String searchTerm = searchTermRaw.toLowerCase();

        return getCachedCryptoData()
                .stream()
                .filter(crypto -> {
                    String name = crypto.name();
                    String symbol = crypto.symbol();
                    return (name != null && name.toLowerCase().contains(searchTerm)) ||
                            (symbol != null && symbol.toLowerCase().contains(searchTerm));
                })
                .toList();
    }

    @Override
    public Map<String, Object> getChartData(String cryptoId, String timeframe) {

        log.info("getChartData called for {}", timeframe);

        return coingeckoService.getCryptoChartData(cryptoId, getDays(timeframe));
    }

    @Override
    public CryptoMarketData getCryptoData(String cryptoId) {

        log.info("getCryptoData called for {}", cryptoId);

        if (!StringUtils.hasText(cryptoId)) {
            throw new IllegalArgumentException("CryptoId is Invalid");
        }

        return redisService.hashGet(RedisKeys.CRYPTO_FULL_HASH, cryptoId, CryptoMarketData.class);
    }

    @Override
    public Map<String, CryptoMarketData> getMultipleCryptoData(Set<String> cryptoIds) {

        log.info("getMultipleCryptoData called for total ids {}", cryptoIds.size());

        Map<String, CryptoMarketData> cryptoDataMap = new HashMap<>();

        for (String id : cryptoIds) {
            CryptoMarketData cryptoMarketData = redisService.hashGet(RedisKeys.CRYPTO_FULL_HASH, id, CryptoMarketData.class);
            cryptoDataMap.put(cryptoMarketData.id(), cryptoMarketData);
        }

        if (cryptoDataMap.isEmpty()) {
            throw new ResourceNotFoundException("Crypto data map by Ids is empty");
        }

        return cryptoDataMap;

    }

    @Override
    public Map<String, BigDecimal> getCryptoPrice(String cryptoId) {

        CryptoMarketData cryptoData = getCryptoData(cryptoId);
        return Map.of("price", cryptoData.current_price());
    }


    private List<CryptoMarketLite> mapToLiteData(List<CryptoMarketData> cryptoList) {

        return cryptoList.stream().map(c -> new CryptoMarketLite(
                c.id(),
                c.symbol(),
                c.name(),
                c.image(),
                c.current_price(),
                c.market_cap(),
                c.market_cap_rank(),
                c.circulating_supply(),
                c.price_change_percentage_24h()
        )).toList();

    }

    public Map<String, String> serializeHashData(List<CryptoMarketData> cryptoMarketData) {

        Map<String, String> cryptoMap = new HashMap<>();

        for (CryptoMarketData crypto : cryptoMarketData) {

            try {
                cryptoMap.put(crypto.id(), objectMapper.writeValueAsString(crypto));
            } catch (Exception e) {
                log.error("Failed to serialize crypto : {}", crypto.id(), e);
            }
        }

        return cryptoMap;
    }


    private String getDays(String timeframe) {

        return switch (timeframe) {
            case "7D" -> "7";
            case "1M" -> "30";
            case "6M" -> "180";
            case "1Y" -> "365";
            default -> "1";
        };


    }

}
