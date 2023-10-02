package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;

public record AirMixingResult(FlowOfHumidAir inletFlow,
                              FlowOfHumidAir recirculationFlow,
                              FlowOfHumidAir outletFlow) {
}