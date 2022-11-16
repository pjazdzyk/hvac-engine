package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record MixingResultsMultipleDto(double pressure,
                                       double outDryAirMassFlow,
                                       double outTemperature,
                                       double outHumidityRatio) {
}