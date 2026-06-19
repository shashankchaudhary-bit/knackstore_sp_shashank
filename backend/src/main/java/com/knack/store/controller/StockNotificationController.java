package com.knack.store.controller;

import com.knack.store.dto.StockNotificationRequest;
import com.knack.store.dto.StockNotificationResponse;
import com.knack.store.service.StockNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Stock Notification", description = "Subscribe to stock alerts for out-of-stock SKUs")
public class StockNotificationController {

    private final StockNotificationService stockNotificationService;

    @PostMapping("/notify_me")
    @Operation(summary = "Subscribe for stock notification", description = "Creates a stock notification subscription for the given email and SKU.")
    public ResponseEntity<StockNotificationResponse> notifyMe(@Valid @RequestBody StockNotificationRequest request) {
        StockNotificationResponse response = stockNotificationService.subscribe(request);
        return ResponseEntity.ok(response);
    }
}

