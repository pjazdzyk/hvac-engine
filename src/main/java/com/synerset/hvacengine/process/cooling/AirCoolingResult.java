package com.synerset.hvacengine.process.cooling;

import com.synerset.hvacengine.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvacengine.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

/**
 * Represents the result of an air cooling process.
 */
record AirCoolingResult(FlowOfHumidAir outletFlow,
                               Power heatOfProcess,
                               FlowOfLiquidWater condensateFlow,
                               BypassFactor bypassFactor) {
}