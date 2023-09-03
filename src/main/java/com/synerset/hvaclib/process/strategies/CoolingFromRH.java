package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.procedures.AirCoolingProcedures;
import com.synerset.hvaclib.process.procedures.dataobjects.AirCoolingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;

record CoolingFromRH(FlowOfHumidAir inletAir,
                     CoolantData inletCoolantData,
                     RelativeHumidity targetRelativeHumidity) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingProcedures.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), targetRelativeHumidity);
    }

}