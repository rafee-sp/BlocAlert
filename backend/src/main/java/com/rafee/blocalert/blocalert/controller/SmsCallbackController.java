package com.rafee.blocalert.blocalert.controller;

import com.rafee.blocalert.blocalert.service.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/callback")
@RequiredArgsConstructor
@Slf4j
public class SmsCallbackController {

    private final SmsService smsService;

    @PostMapping("/message")
    public ResponseEntity<Void> handleSmsStatusCallback(@RequestHeader(value = "X-Twilio-Signature", required = false) String signature,
                                                        @RequestParam String alertId,
                                                        HttpServletRequest request) {

        log.info("handleSmsStatusCallback controller called");

        // Extract ONLY Twilio's POST params (NOT query params)
        Map<String, String> postParams = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            // Skip query parameters
            if (alertId == null || !k.equals("alertId")) {
                postParams.put(k, v[0]);
            }
        });


        if (!smsService.isValidTwilioResponse(postParams, signature, request)) {
            log.error("Text callback Signature validation failed");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        smsService.handleSmsMessageCallback(alertId, postParams);

        return ResponseEntity.ok().build();

    }

}
