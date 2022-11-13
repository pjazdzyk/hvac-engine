package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record MixingResultDto(double inDryAirMassFlow,
                              double recDryAirMassFlow,
                              double outletDryAirMassFlow,
                              double outTemperature,
                              double outHumidityRatio) {
}