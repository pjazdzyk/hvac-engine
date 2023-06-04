package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.Fluid;

public interface Flow<K extends Fluid> {
    K getFluid();

    double getMassFlow();

    double getVolFlow();
}
