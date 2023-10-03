package com.synerset.hvacengine.process.mixing;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;

import java.util.List;

/**
 * The AirMixingResult record represents the result of a mixing process.
 */
public record AirMixingResult(FlowOfHumidAir inletFlow,
                              List<FlowOfHumidAir> recirculationFlows,
                              FlowOfHumidAir outletFlow) {
}