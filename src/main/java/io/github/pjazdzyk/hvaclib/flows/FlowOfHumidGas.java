package io.github.pjazdzyk.hvaclib.flows;

import io.github.pjazdzyk.hvaclib.fluids.HumidGas;

public interface FlowOfHumidGas extends FlowOfFluid<HumidGas>, HumidGas {
    HumidGas getHumidGas();

    double getMassFlowDa();

    double getVolFlowDa();
}
