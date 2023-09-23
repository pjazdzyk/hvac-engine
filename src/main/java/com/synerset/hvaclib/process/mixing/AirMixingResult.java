package com.synerset.hvaclib.process.mixing;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;

public record AirMixingResult(FlowOfHumidAir inletFlow,
                              FlowOfHumidAir recirculationFlow,
                              FlowOfHumidAir outletFlow) {
}