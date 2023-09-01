package com.synerset.hvaclib.process.equations.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidAir;

public record AirMixingResult(FlowOfHumidAir inletFlow,
                              FlowOfHumidAir recirculationFlow,
                              FlowOfHumidAir outletFlow) {
}