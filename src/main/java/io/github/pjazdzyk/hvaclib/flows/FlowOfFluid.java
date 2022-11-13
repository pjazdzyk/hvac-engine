package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.properties.Fluid;

public interface FlowOfFluid<K extends Fluid> extends Fluid {
    K getFluid();

    String getName();

    double getMassFlow();

    double getVolFlow();
}
