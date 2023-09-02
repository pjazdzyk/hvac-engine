package com.synerset.hvaclib.process.procedures.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record AirHeatingResult(FlowOfHumidAir outletFlow,
                               Power heatOfProcess) {
}
