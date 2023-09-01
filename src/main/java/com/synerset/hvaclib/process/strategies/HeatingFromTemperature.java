package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.equations.AirHeatingEquations;
import com.synerset.hvaclib.process.equations.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

record HeatingFromTemperature(FlowOfHumidAir inletAir,
                              Temperature targetTemperature) implements HeatingStrategy {

    HeatingFromTemperature {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(targetTemperature);
    }

    @Override
    public AirHeatingResult applyHeating() {
        return AirHeatingEquations.processOfHeating(inletAir, targetTemperature);
    }

}