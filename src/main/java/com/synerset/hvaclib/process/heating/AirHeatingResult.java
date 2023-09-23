package com.synerset.hvaclib.process.heating;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

public record AirHeatingResult(FlowOfHumidAir outletFlow,
                               Power heatOfProcess) {
}
