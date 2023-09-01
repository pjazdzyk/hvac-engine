package com.synerset.hvaclib.process.strategies;

import com.synerset.hvaclib.flows.FlowOfHumidAir;
import com.synerset.hvaclib.process.dataobjects.CoolantData;
import com.synerset.hvaclib.process.equations.dataobjects.AirCoolingResult;
import com.synerset.unitility.unitsystem.humidity.RelativeHumidity;
import com.synerset.unitility.unitsystem.thermodynamic.Power;
import com.synerset.unitility.unitsystem.thermodynamic.Temperature;

public interface CoolingStrategy {

    AirCoolingResult applyCooling();

    FlowOfHumidAir inletAir();

    CoolantData inletCoolantData();

    static CoolingStrategy of(FlowOfHumidAir inletAir, CoolantData inletCoolantData, Power inputPower) {
        return new CoolingFromPower(inletAir, inletCoolantData, inputPower);
    }

    static CoolingStrategy of(FlowOfHumidAir inletAir, CoolantData inletCoolantData, RelativeHumidity targetRH) {
        return new CoolingFromRH(inletAir, inletCoolantData, targetRH);
    }

    static CoolingStrategy of(FlowOfHumidAir inletAir, CoolantData inletCoolantData, Temperature targetTemperature) {
        return new CoolingFromTemperature(inletAir, inletCoolantData, targetTemperature);
    }

}