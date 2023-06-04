package com.synerset.hvaclib.flows;

import com.synerset.hvaclib.fluids.HumidGas;

public interface FlowOfHumidGas extends Flow<HumidGas> {

    double getMassFlowDa();

    double getVolFlowDa();
}
