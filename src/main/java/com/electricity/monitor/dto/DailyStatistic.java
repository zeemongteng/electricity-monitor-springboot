package com.electricity.monitor.dto;

import java.time.LocalDate;

public record DailyStatistic(
    LocalDate date, String meterId, double totalKwh,
    double averageKwhPerReading, double maxKwhPerReading, long readingCount
) {}
