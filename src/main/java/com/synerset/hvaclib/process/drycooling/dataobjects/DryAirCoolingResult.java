package com.synerset.hvaclib.process.drycooling.dataobjects;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record DryAirCoolingResult(FlowOfHumidAir outletFlow,
                                  Power heatOfProcess) {
}