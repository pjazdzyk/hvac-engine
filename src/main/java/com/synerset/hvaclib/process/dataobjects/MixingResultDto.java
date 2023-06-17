package com.synerset.hvaclib.process.dataobjects;

public record MixingResultDto(double pressure,
                              double outTemperature,
                              double outHumidityRatio,
                              double outDryAirMassFlow,
                              double inDryAirMassFlow,
                              double recDryAirMassFlow) implements BasicResults {
}