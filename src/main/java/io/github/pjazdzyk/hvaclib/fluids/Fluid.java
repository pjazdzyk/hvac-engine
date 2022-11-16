package io.github.pjazdzyk.hvaclib.fluids;

import io.github.pjazdzyk.hvaclib.flows.TypeOfFluidFlow;

public interface Fluid {

    double getAbsPressure();

    double getTemp();

    double getDensity();

    double getSpecHeatCP();

    double getSpecEnthalpy();

}
