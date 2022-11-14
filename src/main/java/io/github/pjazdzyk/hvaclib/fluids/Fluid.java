package io.github.pjazdzyk.hvaclib.fluids;

public interface Fluid {
    String getName();

    double getAbsPressure();

    double getTemp();

    double getDensity();

    double getSpecHeatCP();

    double getSpecEnthalpy();
}
