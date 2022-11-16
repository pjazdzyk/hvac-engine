package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record BasicResultsDto(double pressure,
                              double outTemperature,
                              double outHumidityRatio,
                              double outDryAirMassFlow) implements BasicResults {
}
