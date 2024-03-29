package com.synerset.hvacengine.process.heating;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

/**
 * Represents the result of an air heating process.
 */
public record AirHeatingResult(FlowOfHumidAir outletFlow,
                               Power heatOfProcess) {
}
