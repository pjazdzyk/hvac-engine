package io.github.pjazdzyk.hvacapi.fluids.dto;

import io.github.pjazdzyk.hvaclib.fluids.VapourState;

public record MoistAirResponseDto(
        VapourState vapourStatus,
        double absPressure,
        double temperature,
        double dewPointTemperature,
        double wetBulbTemperature,
        double relativeHumidity,
        double humidityRatioX,
        double maxHumidityRatioX,
        double density,
        double specHeat,
        double specEnthalpy,
        double thermalConductivity,
        double thermalDiffusivity,
        double dynamicViscosity,
        double kinematicViscosity,
        double prandtlNumber,
        String classOfFluid
) {}
