package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.procedures.AirHeatingProcedures;
import com.synerset.hvaclib.process.procedures.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;

record HeatingFromRH(FlowOfHumidAir inletAir,
                     RelativeHumidity targetRelativeHumidity) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {
        return AirHeatingProcedures.processOfHeating(inletAir, targetRelativeHumidity);
    }

}