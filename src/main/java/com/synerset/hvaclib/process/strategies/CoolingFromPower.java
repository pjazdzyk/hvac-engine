package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.equations.AirCoolingEquations;
import com.synerset.hvaclib.process.equations.dataobjects.AirCoolingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Power;

record CoolingFromPower(FlowOfHumidAir inletAir,
                        CoolantData inletCoolantData,
                        Power inputPower) implements CoolingStrategy {

    CoolingFromPower {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(inputPower);
        Validators.requireNotNull(inletCoolantData);
    }

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingEquations.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), inputPower);
    }


}