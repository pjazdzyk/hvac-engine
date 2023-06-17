package com.synerset.hvaclib.process.dataobjects;

public record HeatingResultDto(double pressure,
                               double outTemperature,
                               double outHumidityRatio,
                               double outDryAirMassFlow,
                               double heatOfProcess) implements BasicResults {
}