package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record HeatCoolResultDto(double heatOfProcess,
                                double outTemperature,
                                double outHumidityRatio,
                                double condensateTemperature,
                                double condensateMassFlow) {
}