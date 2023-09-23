package com.synerset.hvaclib.process.cooling;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.fluids.liquidwater.FlowOfLiquidWater;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record AirCoolingResult(FlowOfHumidAir outletFlow,
                               Power heatOfProcess,
                               FlowOfLiquidWater condensateFlow,
                               BypassFactor bypassFactor) {
}