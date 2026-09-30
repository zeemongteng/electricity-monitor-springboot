package com.electricity.monitor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public record ReadingRequest(
    @NotBlank String meterId,
    OffsetDateTime timestamp,
    @NotNull Double voltage,
    @NotNull Double current,
    @NotNull Double powerWatts,
    @NotNull Double energyKwh
) {}
