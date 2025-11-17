package com.rafee.blocalert.blocalert.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketData;
import com.rafee.blocalert.blocalert.DTO.internal.MarketStatsData;
import com.rafee.blocalert.blocalert.exception.ExternalApiException;
import com.rafee.blocalert.blocalert.service.CoingeckoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoingeckoServiceImpl implements CoingeckoService {

    @Value("${coingecko.url}")
    private String baseUrl;

    @Value("${coingecko.secret}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    @Retryable(
            value = {ExternalApiException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public List<CryptoMarketData> fetchCryptoMarketData() {

        log.info("fetchCryptoMarketData called");

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/coins/markets")
                .queryParam("vs_currency", "usd")
                .queryParam("order", "market_cap_desc")
                .queryParam("per_page", 250)
                .queryParam("page", 1)
                .queryParam("sparkline", false)
                .toUriString();

        RestClient client = initializeRestClient();

        try {
            ResponseEntity<CryptoMarketData[]> response = client.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(CryptoMarketData[].class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Coingecko API  for fetchCryptoMarketData returned non-success status: {}", response.getStatusCode());
                throw new ExternalApiException("Coingecko returned non-success status: " + response.getStatusCode());
            }

            if (response.getBody() == null || response.getBody().length == 0) {
                log.warn("No crypto data returned from Coingecko (URL: {})", url);
                return Collections.emptyList();
            }

            log.info("Fetched {} cryptos from Coingecko", response.getBody().length);
            return Arrays.asList(response.getBody());

        } catch (RestClientResponseException e) {

            log.error("Coingecko API for fetchCryptoMarketData returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException("Coingecko API error: " + e.getStatusCode() + " - " + e.getStatusText());

        } catch (Exception e) {
            log.error("Unexpected error while fetching crypto data from Coingecko", e);
            throw new ExternalApiException("Unexpected error while fetching crypto data");
        }
    }


    @Retryable(
            value = {ExternalApiException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public MarketStatsData fetchMarketStats() {

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/global")
                .toUriString();

        RestClient client = initializeRestClient();

        try {
            ResponseEntity<String> response = client.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Coingecko API for fetchMarketStats returned non-success status: {}", response.getStatusCode());
                throw new ExternalApiException("Coingecko returned non-success status: " + response.getStatusCode());
            }

            if (!StringUtils.hasText(response.getBody())) {
                log.warn("No market data returned from Coingecko (URL: {})", url);
                throw new ExternalApiException("No market data returned from Coingecko ");
            }

            log.info("Fetched market stats from Coingecko");

            JsonNode marketDataNode = objectMapper.readTree(response.getBody()).path("data");

            return MarketStatsData.from(marketDataNode);

        } catch (RestClientResponseException e) {

            log.error("Coingecko API for fetchMarketStats returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException("Coingecko API error: " + e.getStatusCode() + " - " + e.getStatusText());

        } catch (Exception e) {
            log.error("Unexpected error while fetching market data from Coingecko", e);
            throw new ExternalApiException("Unexpected error while fetching market data");
        }
    }

    @Retryable(
            value = {ExternalApiException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    @Override
    public Map<String, Object> getCryptoChartData(String cryptoId, String timeframe) {

        log.info("getCryptoChartData called for {} - {}", cryptoId, timeframe);

        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .pathSegment("coins", cryptoId, "market_chart")
                .queryParam("vs_currency", "usd")
                .queryParam("days", timeframe)
                .toUriString();

        RestClient client = initializeRestClient();

        try {
            ResponseEntity<Map> response = client.get()
                    .uri(url)
                    .retrieve()
                    .toEntity(Map.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Coingecko API for getCryptoChartData returned non-success status: {}", response.getStatusCode());
                throw new ExternalApiException("Coingecko returned non-success status: " + response.getStatusCode());
            }

            if (response.getBody().isEmpty()) {
                log.warn("No crypto chart data returned from Coingecko (URL: {})", url);
                throw new ExternalApiException("No market data returned from Coingecko ");
            }

            log.info("Fetched crypto chart data from Coingecko");

            return response.getBody();

        } catch (RestClientResponseException e) {

            log.error("Coingecko API for getCryptoChartData returned error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException("Coingecko API error: " + e.getStatusCode() + " - " + e.getStatusText());

        } catch (Exception e) {
            log.error("Unexpected error while fetching market data from Coingecko", e);
            throw new ExternalApiException("Unexpected error while fetching market data");
        }
    }

    RestClient initializeRestClient() {

        return RestClient.builder()
                .defaultHeader("Accept", "application/json")
                .defaultHeader("x-cg-demo-api-key", apiKey)
                .build();
    }

}
