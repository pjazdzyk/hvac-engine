package com.synerset.hvaclib.process.equations.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.unitility.unitsystem.dimensionless.BypassFactor;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record AirCoolingResult(FlowOfHumidAir outletFlow,
                               Power heatOfProcess,
                               FlowOfWater condensateFlow,
                               BypassFactor bypassFactor) {
}