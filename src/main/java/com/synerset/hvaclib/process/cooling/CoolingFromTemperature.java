package com.synerset.hvaclib.process.cooling;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.process.cooling.dataobjects.AirCoolingResult;
import com.synerset.hvaclib.process.cooling.dataobjects.CoolantData;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

record CoolingFromTemperature(FlowOfHumidAir inletAir,
                              CoolantData inletCoolantData,
                              Temperature outletTemperature) implements CoolingStrategy {

    @Override
    public AirCoolingResult applyCooling() {
        return AirCoolingProcedures.processOfRealCooling(inletAir, inletCoolantData.getAverageTemperature(), outletTemperature);
    }

}