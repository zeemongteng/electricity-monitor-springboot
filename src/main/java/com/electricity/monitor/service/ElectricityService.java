package com.electricity.monitor.service;

import com.electricity.monitor.dto.*;
import com.electricity.monitor.entity.*;
import com.electricity.monitor.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;

@Service
public class ElectricityService {
    private final ElectricityReadingRepository readings;
    private final NotificationRepository notifications;

    @Value("${app.anomaly.multiplier:1.5}")
    private double anomalyMultiplier;
    @Value("${app.anomaly.minimum-history:6}")
    private int minimumHistory;

    private final ZoneId zone = ZoneId.of("Asia/Bangkok");

    public ElectricityService(ElectricityReadingRepository readings,
                              NotificationRepository notifications) {
        this.readings = readings;
        this.notifications = notifications;
    }

    @Transactional
    public ElectricityReading saveReading(ReadingRequest req) {
        OffsetDateTime time = req.timestamp()!=null ? req.timestamp() :
            OffsetDateTime.now(zone);

        ElectricityReading saved = readings.save(ElectricityReading.builder()
            .meterId(req.meterId()).timestamp(time)
            .voltage(req.voltage()).current(req.current())
            .powerWatts(req.powerWatts()).energyKwh(req.energyKwh()).build());

        checkAnomaly(saved);
        return saved;
    }

    private void checkAnomaly(ElectricityReading current) {
        List<ElectricityReading> history = readings.findHistory(
            current.getMeterId(), current.getTimestamp(),
            PageRequest.of(0, Math.max(minimumHistory,100)));

        if (history.size() < minimumHistory) return;

        double avg = history.stream()
            .mapToDouble(ElectricityReading::getEnergyKwh).average().orElse(0);

        if (avg > 0 && current.getEnergyKwh() > avg * anomalyMultiplier) {
            notifications.save(Notification.builder()
                .meterId(current.getMeterId()).timestamp(current.getTimestamp())
                .type("HIGH_USAGE")
                .message(String.format(
                    "การใช้ไฟฟ้าสูงกว่าค่าเฉลี่ย %.2f เท่า: %.4f kWh (ค่าเฉลี่ย %.4f kWh)",
                    current.getEnergyKwh()/avg, current.getEnergyKwh(), avg))
                .currentKwh(current.getEnergyKwh()).baselineKwh(avg).read(false).build());
        }
    }

    public List<ElectricityReading> getReadings(String meterId, LocalDate date) {
        OffsetDateTime from=date.atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime to=date.plusDays(1).atStartOfDay(zone).toOffsetDateTime();
        return readings.findByMeterIdAndTimestampBetweenOrderByTimestampAsc(meterId,from,to);
    }

    public DailyStatistic getDailyStatistic(String meterId, LocalDate date) {
        List<ElectricityReading> rs=getReadings(meterId,date);
        if(rs.isEmpty()) return new DailyStatistic(date,meterId,0,0,0,0);
        double total=rs.stream().mapToDouble(ElectricityReading::getEnergyKwh).sum();
        double avg=total/rs.size();
        double max=rs.stream().mapToDouble(ElectricityReading::getEnergyKwh).max().orElse(0);
        return new DailyStatistic(date,meterId,total,avg,max,rs.size());
    }

    public List<Notification> getNotifications(boolean unreadOnly) {
        return unreadOnly ? notifications.findTop50ByReadFalseOrderByTimestampDesc()
                           : notifications.findTop50ByOrderByTimestampDesc();
    }

    @Transactional
    public void markRead(Long id) {
        notifications.findById(id).ifPresent(n->n.setRead(true));
    }
}
