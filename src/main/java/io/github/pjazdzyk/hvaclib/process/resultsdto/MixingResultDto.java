package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record MixingResultDto(double pressure,
                              double outTemperature,
                              double outHumidityRatio,
                              double outDryAirMassFlow,
                              double inDryAirMassFlow,
                              double recDryAirMassFlow) implements BasicResults {
}