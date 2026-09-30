package com.electricity.monitor.repository;

import com.electricity.monitor.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findTop50ByOrderByTimestampDesc();
    List<Notification> findTop50ByReadFalseOrderByTimestampDesc();
}
