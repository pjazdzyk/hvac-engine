package com.synerset.hvaclib.process.dataobjects;

public interface BasicResults {
    double pressure();

    double outTemperature();

    double outHumidityRatio();

    double outDryAirMassFlow();
}
