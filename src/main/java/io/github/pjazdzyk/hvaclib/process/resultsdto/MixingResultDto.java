package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record MixingResultDto(double Pressure,
                              double inDryAirMassFlow,
                              double recDryAirMassFlow,
                              double outletDryAirMassFlow,
                              double outTemperature,
                              double outHumidityRatio) {
}