package com.rafee.blocalert.blocalert.controller;

import com.rafee.blocalert.blocalert.DTO.request.AlertRequest;
import com.rafee.blocalert.blocalert.DTO.response.ActiveAlertResponse;
import com.rafee.blocalert.blocalert.DTO.response.ApiResponse;
import com.rafee.blocalert.blocalert.DTO.response.PastAlertResponse;
import com.rafee.blocalert.blocalert.service.AlertService;
import com.rafee.blocalert.blocalert.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AuthenticationService authenticationService;
    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<Void> addAlert(@RequestBody @Valid AlertRequest request){

        log.debug("addAlert called");

        Long userId = authenticationService.getCurrentUserId();

        alertService.addAlert(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAlert(@PathVariable Long id, @RequestBody @Valid AlertRequest request) throws AccessDeniedException {

        log.debug("updateAlert called");

        Long userId = authenticationService.getCurrentUserId();

        alertService.updateAlert(id, userId, request);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActiveAlerts(@RequestParam(required = false) String cryptoId, Pageable pageable) {

        log.debug("getActiveAlerts called");

        Long userId = authenticationService.getCurrentUserId();

        Page<ActiveAlertResponse> activeAlertsPage = alertService.getActiveAlerts(userId, cryptoId, pageable);

        List<ActiveAlertResponse> activeAlertsList = activeAlertsPage.getContent();

        if (activeAlertsList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse("No Active alerts found", activeAlertsList));
        }

        return ResponseEntity.ok().body(new ApiResponse("Active alerts fetched", activeAlertsList, activeAlertsPage));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse> getPastAlerts(@RequestParam(required = false) String cryptoId, Pageable pageable) {

        log.debug("getPastAlerts called");

        Long userId = authenticationService.getCurrentUserId();

        Page<PastAlertResponse> pastAlertsPage = alertService.getPastAlerts(userId, cryptoId, pageable);

        List<PastAlertResponse> pastAlertsList = pastAlertsPage.getContent();

        if (pastAlertsList.isEmpty()) {
            return ResponseEntity.ok().body(new ApiResponse("No Past alerts found", pastAlertsList));
        }

        return ResponseEntity.ok().body(new ApiResponse("Past alerts fetched", pastAlertsList, pastAlertsPage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteAlert(@PathVariable Long id) throws AccessDeniedException {

        log.debug("deleteAlert called");

        Long userId = authenticationService.getCurrentUserId();
        alertService.deleteAlert(id, userId);

        return ResponseEntity.noContent().build();
    }

}
