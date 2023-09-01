package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.equations.AirHeatingEquations;
import com.synerset.hvaclib.process.equations.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;

record HeatingFromRH(FlowOfHumidAir inletAir,
                     RelativeHumidity targetRelativeHumidity) implements HeatingStrategy {

    HeatingFromRH {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(targetRelativeHumidity);
    }

    @Override
    public AirHeatingResult applyHeating() {
        return AirHeatingEquations.processOfHeating(inletAir, targetRelativeHumidity);
    }

}