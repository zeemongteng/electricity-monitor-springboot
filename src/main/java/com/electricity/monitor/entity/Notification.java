package com.electricity.monitor.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Entity
@Table(name="notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(name="meter_id", nullable=false)
    private String meterId;
    @Column(nullable=false)
    private OffsetDateTime timestamp;
    @Column(nullable=false, length=32)
    private String type;
    @Column(nullable=false, length=500)
    private String message;
    @Column(name="current_kwh")
    private Double currentKwh;
    @Column(name="baseline_kwh")
    private Double baselineKwh;
    @Column(nullable=false)
    private Boolean read=false;
}
