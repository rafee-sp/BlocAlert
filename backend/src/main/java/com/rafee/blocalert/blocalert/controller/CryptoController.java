package com.rafee.blocalert.blocalert.controller;

import com.rafee.blocalert.blocalert.DTO.internal.CryptoMarketLite;
import com.rafee.blocalert.blocalert.DTO.response.ApiResponse;
import com.rafee.blocalert.blocalert.service.CryptoService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cryptos")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CryptoController {

    private final CryptoService cryptoService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchCrypto(@RequestParam @NotBlank(message = "Search term is required") String searchTerm){

        log.info("searchCrypto called");

        List<CryptoMarketLite> cryptoMarketLiteList = cryptoService.searchCrypto(searchTerm);

        return ResponseEntity.ok().body(new ApiResponse("Search success", cryptoMarketLiteList));
    }

    @GetMapping("/{id}/chart")
    public ResponseEntity<ApiResponse> getChartData(@PathVariable String id,
                                                    @RequestParam @NotBlank(message = "Timeframe is required")  String timeframe){

        log.info("getChartData called");

        Map<String, Object> chartDataMap = cryptoService.getChartData(id, timeframe);

        return ResponseEntity.ok().body(new ApiResponse("Chart data fetched", chartDataMap));
    }

    @GetMapping("/price")
    public ResponseEntity<ApiResponse> getCryptoPrice(@RequestParam @NotBlank(message = "CryptoId is required") String cryptoId){

        log.info("getCryptoPrice called");

        Map<String, BigDecimal> cryptoPrice = cryptoService.getCryptoPrice(cryptoId);

        return ResponseEntity.ok().body(new ApiResponse("Crypto price fetched", cryptoPrice));
    }

}
