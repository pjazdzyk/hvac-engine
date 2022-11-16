package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record HeatingResultDto(double pressure,
                               double outTemperature,
                               double outHumidityRatio,
                               double outDryAirMassFlow,
                               double heatOfProcess) implements BasicResults {
}