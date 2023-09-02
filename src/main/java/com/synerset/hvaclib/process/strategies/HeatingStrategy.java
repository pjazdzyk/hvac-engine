package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.procedures.dataobjects.AirHeatingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public interface HeatingStrategy {

    AirHeatingResult applyHeating();

    FlowOfHumidAir inletAir();

    static HeatingStrategy of(FlowOfHumidAir inletAir, Power inputPower) {
        return new HeatingFromPower(inletAir, inputPower);
    }

    static HeatingStrategy of(FlowOfHumidAir inletAir, RelativeHumidity targetRelativeHumidity) {
        return new HeatingFromRH(inletAir, targetRelativeHumidity);
    }

    static HeatingStrategy of(FlowOfHumidAir inletAir, Temperature targetTemperature) {
        return new HeatingFromTemperature(inletAir, targetTemperature);
    }

}