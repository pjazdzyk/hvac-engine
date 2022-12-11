package io.github.pjazdzyk.hvacapi.fluids;

import io.github.pjazdzyk.hvacapi.fluids.dto.FluidResponseDto;
import io.github.pjazdzyk.hvacapi.fluids.dto.MoistAirResponseDto;

public interface FluidService {

    FluidResponseDto createWaterProperties(double temp);

    MoistAirResponseDto createMoistAirProperty(Double absPressure, double dryBulbTemp, double humidityRatio);

    double convertRHtoHumRatio(double absPressure, double dryBulbTemp, double relHum);

}
