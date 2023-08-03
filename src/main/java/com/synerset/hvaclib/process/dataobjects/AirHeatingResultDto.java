package com.synerset.hvaclib.process.dataobjects;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record AirHeatingResultDto(FlowOfHumidAir outletFlow,
                                  Power heatOfProcess) {
}
