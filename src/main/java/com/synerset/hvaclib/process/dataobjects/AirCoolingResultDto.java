package com.synerset.hvaclib.process.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.flows.FlowOfWater;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record AirCoolingResultDto(FlowOfHumidAir outletFlow,
                                  Power heatOfProcess,
                                  FlowOfWater condensateFlow) {
}