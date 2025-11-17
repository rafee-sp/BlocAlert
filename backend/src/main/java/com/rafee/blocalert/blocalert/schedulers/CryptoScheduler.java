package com.rafee.blocalert.blocalert.schedulers;

import com.rafee.blocalert.blocalert.service.CryptoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CryptoScheduler {

    private final CryptoService cryptoService;

    @Scheduled(fixedDelay = 65000, initialDelay = 10000)
    public void fetchCryptoData() {
        log.info("fetchCryptoData scheduler started at : {}", LocalDateTime.now());
        cryptoService.fetchAndCacheCryptoData();
    }

    @Scheduled(fixedRate = 30 * 60 * 1000, initialDelay = 10000)
    public void fetchMarketStats() {
        log.info("fetchMarketStats scheduler started at : {}", LocalDateTime.now());
        cryptoService.fetchAndCacheMarketStats();
    }

}
