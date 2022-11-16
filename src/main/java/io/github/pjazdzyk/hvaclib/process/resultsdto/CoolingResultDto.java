package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record CoolingResultDto(double pressure,
                               double outTemperature,
                               double outHumidityRatio,
                               double heatOfProcess,
                               double condensateTemperature,
                               double condensateMassFlow) {
}