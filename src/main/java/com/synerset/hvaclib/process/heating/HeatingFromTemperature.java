package com.synerset.hvaclib.process.heating;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.process.heating.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

record HeatingFromTemperature(FlowOfHumidAir inletAir,
                              Temperature targetTemperature) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {
        return AirHeatingProcedures.processOfHeating(inletAir, targetTemperature);
    }

}