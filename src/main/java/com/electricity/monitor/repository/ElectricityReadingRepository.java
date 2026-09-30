package com.electricity.monitor.repository;

import com.electricity.monitor.entity.ElectricityReading;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.OffsetDateTime;
import java.util.List;

public interface ElectricityReadingRepository extends JpaRepository<ElectricityReading,Long> {
    List<ElectricityReading> findByMeterIdAndTimestampBetweenOrderByTimestampAsc(
        String meterId, OffsetDateTime from, OffsetDateTime to);

    @Query("SELECT r FROM ElectricityReading r " +
           "WHERE r.meterId=:meterId AND r.timestamp < :timestamp " +
           "ORDER BY r.timestamp DESC")
    List<ElectricityReading> findHistory(String meterId, OffsetDateTime timestamp, Pageable pageable);
}
