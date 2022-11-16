package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record CoolingResultDto(double pressure,
                               double heatOfProcess,
                               double outTemperature,
                               double outHumidityRatio,
                               double condensateTemperature,
                               double condensateMassFlow) {
}