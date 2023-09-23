package com.synerset.hvaclib.process.heating;

import com.synerset.hvaclib.fluids.humidair.FlowOfHumidAir;
import com.synerset.hvaclib.process.heating.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;

record HeatingFromRH(FlowOfHumidAir inletAir,
                     RelativeHumidity targetRelativeHumidity) implements HeatingStrategy {

    @Override
    public AirHeatingResult applyHeating() {
        return AirHeatingProcedures.processOfHeating(inletAir, targetRelativeHumidity);
    }

}