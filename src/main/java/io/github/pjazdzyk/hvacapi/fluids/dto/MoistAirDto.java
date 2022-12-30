package io.github.pjazdzyk.hvacapi.fluids.dto;

public record MoistAirDto(
        double absPressure,
        double dryBulbTemperature,
        double humidityRatioX) {
}
