package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.equations.AirCoolingEquations;
import com.synerset.hvaclib.process.equations.dataobjects.AirCoolingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

record CoolingFromTemperature(FlowOfHumidAir inletAir,
                              CoolantData inletCoolantData,
                              Temperature outletTemperature) implements CoolingStrategy {

    CoolingFromTemperature {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(outletTemperature);
        Validators.requireNotNull(inletCoolantData);
    }

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingEquations.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), outletTemperature);
    }

}