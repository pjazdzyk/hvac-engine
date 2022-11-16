package io.github.pjazdzyk.hvaclib.process.resultsdto;

public record HeatingResultDto(double pressure,
                               double heatOfProcess,
                               double outTemperature,
                               double outHumidityRatio) {
}