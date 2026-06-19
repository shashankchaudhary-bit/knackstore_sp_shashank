package com.knack.store.repository;
import com.knack.store.model.StockNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface StockNotificationRepository extends JpaRepository<StockNotification, Long> {
    Optional<StockNotification> findByUserIdAndSku(Long userId, String sku);
}
