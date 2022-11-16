package io.github.pjazdzyk.hvaclib.process.resultsdto;

public interface BasicResults {
    double pressure();

    double outTemperature();

    double outHumidityRatio();

    double outDryAirMassFlow();
}
