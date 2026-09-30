package com.electricity.monitor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="electricity_readings", indexes={
    @Index(name="idx_reading_meter_time", columnList="meter_id,timestamp"),
    @Index(name="idx_reading_timestamp", columnList="timestamp")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ElectricityReading {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="meter_id", nullable=false, length=64)
    private String meterId;
    @Column(nullable=false)
    private OffsetDateTime timestamp;
    @Column(nullable=false)
    private Double voltage;
    @Column(nullable=false)
    private Double current;
    @Column(name="power_watts", nullable=false)
    private Double powerWatts;
    @Column(name="energy_kwh", nullable=false)
    private Double energyKwh;
}
