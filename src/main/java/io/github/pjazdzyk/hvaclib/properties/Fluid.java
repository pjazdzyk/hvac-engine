package io.github.pjazdzyk.hvaclib.properties;

public interface Fluid {
    String getName();

    double getPressure();

    double getTemp();

    double getDensity();

    double getSpecHeatCP();

    double getSpecEnthalpy();
}
