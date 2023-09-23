package com.synerset.hvaclib.process.cooling;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.process.cooling.dataobjects.AirCoolingResult;
import com.synerset.hvaclib.process.cooling.dataobjects.CoolantData;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;

record CoolingFromRH(FlowOfHumidAir inletAir,
                     CoolantData inletCoolantData,
                     RelativeHumidity targetRelativeHumidity) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingProcedures.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), targetRelativeHumidity);
    }

}