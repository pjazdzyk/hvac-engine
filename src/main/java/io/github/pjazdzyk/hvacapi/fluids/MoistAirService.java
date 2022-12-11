package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;

public interface MoistAirService {
    MoistAirResponseDto createMoistAirProperty(Double absPressure, double dryBulbTemp, double humidityRatio);
}
