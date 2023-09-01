package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.exceptionhandling.Validators;
import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.equations.AirCoolingEquations;
import com.synerset.hvaclib.process.equations.dataobjects.AirCoolingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;

record CoolingFromRH(FlowOfHumidAir inletAir,
                     CoolantData inletCoolantData,
                     RelativeHumidity relativeHumidity) implements CoolingStrategy {

    CoolingFromRH {
        Validators.requireNotNull(inletAir);
        Validators.requireNotNull(relativeHumidity);
        Validators.requireNotNull(inletCoolantData);
    }

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingEquations.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), relativeHumidity);
    }

}