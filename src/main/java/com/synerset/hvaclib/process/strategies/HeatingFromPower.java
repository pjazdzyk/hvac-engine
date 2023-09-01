package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.equations.AirHeatingEquations;
import com.synerset.hvaclib.process.equations.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

record HeatingFromPower(FlowOfHumidAir inletAir,
                        Power inputPower) implements HeatingStrategy {

    HeatingFromPower {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(inputPower);
    }

    @Override
    public AirHeatingResult applyHeating() {
        return AirHeatingEquations.processOfHeating(inletAir, inputPower);
    }

}