package process.dataobjects;

public record CoolingResultDto(double pressure,
                               double outTemperature,
                               double outHumidityRatio,
                               double outDryAirMassFlow,
                               double heatOfProcess,
                               double condensateTemperature,
                               double condensateMassFlow) implements BasicResults {
}