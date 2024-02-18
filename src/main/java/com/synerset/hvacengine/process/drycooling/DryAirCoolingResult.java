package com.synerset.hvacengine.process.drycooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

/**
 * Represents the result of an air dry cooling process.
 */
public record DryAirCoolingResult(FlowOfHumidAir outletFlow,
                                  Power heatOfProcess) {
}