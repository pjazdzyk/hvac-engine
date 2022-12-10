package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.Fluid;

public interface Flow<K extends Fluid> {
    K getFluid();

    double getMassFlow();

    double getVolFlow();
}