package io.github.pjazdzyk.hvaclib.fluids;

public interface Fluid {

    double getAbsPressure();

    double getTemp();

    double getDensity();

    double getSpecHeatCP();

    double getSpecEnthalpy();

}
