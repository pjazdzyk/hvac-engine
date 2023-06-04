package com.synerset.hvaclib.process.resultsdto;

public record BasicResultsDto(double pressure,
                              double outTemperature,
                              double outHumidityRatio,
                              double outDryAirMassFlow) implements BasicResults {
}
