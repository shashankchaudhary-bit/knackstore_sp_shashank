package com.knack.store.service;

import com.knack.store.dto.StockNotificationRequest;
import com.knack.store.dto.StockNotificationResponse;
import com.knack.store.model.Customer;
import com.knack.store.model.StockNotification;
import com.knack.store.repository.CustomerRepository;
import com.knack.store.repository.StockNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockNotificationService {

    private final StockNotificationRepository stockNotificationRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public StockNotificationResponse subscribe(StockNotificationRequest request) {
        try {
            // Find customer by email
            Customer customer = customerRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Customer not found with email: " + request.getEmail()));

            // Check if notification already exists for this user and SKU
            var existingNotification = stockNotificationRepository.findByUserIdAndSku(customer.getId(), request.getSku());

            if (request.isSubscribeNow()) {
                // If subscribeNow is true, create entry if it doesn't exist, or return existing
                return existingNotification
                        .map(existing -> StockNotificationResponse.builder()
                                .success(true)
                                .message("Already subscribed to notifications for this SKU")
                                .notificationId(existing.getId())
                                .alreadySubscribed(true)
                                .exists(true)
                                .build())
                        .orElseGet(() -> {
                            // Create new notification
                            StockNotification created = stockNotificationRepository.save(StockNotification.builder()
                                    .userId(customer.getId())
                                    .sku(request.getSku())
                                    .email(request.getEmail())
                                    .notificationStatus("PENDING")
                                    .build());

                            return StockNotificationResponse.builder()
                                    .success(true)
                                    .message("Successfully subscribed to stock notifications")
                                    .notificationId(created.getId())
                                    .alreadySubscribed(false)
                                    .exists(true)
                                    .build();
                        });
            } else {
                // If subscribeNow is false, just check if entry exists
                return existingNotification
                        .map(existing -> StockNotificationResponse.builder()
                                .success(true)
                                .message("Notification entry exists for this user and SKU")
                                .notificationId(existing.getId())
                                .exists(true)
                                .build())
                        .orElseGet(() -> StockNotificationResponse.builder()
                                .success(true)
                                .message("No notification entry found for this user and SKU")
                                .exists(false)
                                .build());
            }
        } catch (Exception e) {
            return StockNotificationResponse.builder()
                    .success(false)
                    .message("Failed to process request: " + e.getMessage())
                    .exists(false)
                    .build();
        }
    }
}



